package com.neutrino.game.domain.model.turn

import com.badlogic.gdx.Gdx
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.map.chunk.CharacterArray
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkCoords
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.hasIdentity
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import squidpony.squidmath.Coord

/**
 * Singleton turn class containing turn and tick data
 */
object Turn {
    /**
     * Current game clock turn. Decides on many in game events
     * Will yeet out Y2K style after reaching its max value, so the player should be incentivized to play less than 5.700447535712569 x 10^300 years when making a turn every second
     */
    var turn: Double = 0.0
        private set

    private fun tick() {
        turn += 0.01
    }

    init {
        setObservers()
    }

    var playerAction: Boolean = false

    var updateBatch: ArrayDeque<Action> = ArrayDeque()
        private set

    private fun setMovementUpdateBatch(moveAction: Action.MOVE) {
        if (updateBatch.firstOrNull() is Action.MOVE)
            updateBatch[0] = moveAction
        else
            updateBatch.addFirst(moveAction)
    }

    /**
     * A list of current level characters. Should be changed / added to when entering a new Level
     */
    // TODO ECS Characters Initialize with Player inside
    var characterArray: CharacterArray = CharacterArray()
    lateinit var currentChunk: Chunk

    /**
     * List containing various short term events, often related to characters.
     * Incorporates cooldowns, buffs, etc.
     */
    var eventArray: EventArray = EventArray()

    /**
     * Stores time dependant global event information
     */
    val globalEventArray: EventArray = EventArray()

    fun unsetLevel() {
        currentChunk.characterArray.clear()
        currentChunk.characterArray.addAll(characterArray)
        currentChunk.characterArray.remove(Player)
    }

    fun setLevel(chunk: Chunk) {
        currentChunk = chunk
        characterArray.addAll(chunk.characterArray)

        for (character in characterArray) {
            character.getSuper(Ai::class)!!.updateFov()
        }

        Player.call(VisionChangedCallable::class)
    }

    fun makeTurn() {
        // character actions
        while (characterArray.get(turn) != null) {
            val character = characterArray.get(turn)!!
            playerAction = character == Player

            if (updateBatch.firstOrNull() == Action.MOVE(
                    character.get(Position::class)!!.x,
                    character.get(Position::class)!!.y)) {
                updateBatch.removeFirst()
                println("resetting update batch")
            }

            // Player actions
            if (playerAction) {
                // Makes the player action or returns if Action.NOTHING
                val action: Action = character.getSuper(Ai::class)!!.useAction()
                when (action) {
                    is Action.NOTHING -> return
                    is Action.MOVE -> {
                        character.get(Position::class)!!.moveCharacter(Position(action.x, action.y, currentChunk))
                        character.getSuper(Ai::class)!!.updateFov()
                        Player.call(VisionChangedCallable::class)
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                        if (currentChunk.map[action.y][action.x] hasIdentity Identity.StairsDown::class)
                            GlobalData.notifyObservers(GlobalDataType.LEVELCHANGED, ChunkCoords(
                                currentChunk.chunkCoords.x,
                                currentChunk.chunkCoords.y,
                                currentChunk.chunkCoords.z - 1
                            )
                            )
                        if (currentChunk.map[action.y][action.x] hasIdentity Identity.StairsUp::class)
                            GlobalData.notifyObservers(GlobalDataType.LEVELCHANGED, ChunkCoords(
                                currentChunk.chunkCoords.x,
                                currentChunk.chunkCoords.y,
                                currentChunk.chunkCoords.z + 1
                            )
                            )
                    }
                    is Action.ATTACK -> {
                        Player.get(OffensiveStats::class)!!.attack(Position(action.x, action.y, currentChunk))
                    }
                    is Action.INTERACTION -> {
                        // Entity position(x, y) can be derived from ai.entityTargetCoords
                        when (action.interaction) {
                            is InteractionType.ITEM -> {
                                // TODO ECS ITEM
                                if (Player.get(Inventory::class)!!.add(action.entity)) {
                                    GlobalData.notifyObservers(GlobalDataType.PICKUP, action.entity)
//                                    ActorVisuals.showPickedUpItem(Player, item)
                                    val coords = Player.getSuper(Ai::class)!!.targetCoords
                                    currentChunk.map[coords!!.second][coords.first].removeLast()
                                } else println("Inventory is full")

                                Player.get(Inventory::class)!!.printAll()
                            }
                            is InteractionType.DESTROY -> {
                                action.entity.get(DefensiveStats::class)!!.getDamage(Player.get(OffensiveStats::class)!!)
                                Player.get(Projectile::class)?.shoot(action.entity.get(Position::class)!!)
                            }
                            is InteractionType.OPEN -> {
//                                currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].remove(action.entity)
//                                for (item in (action.entity as Container).itemList) {
//                                    currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].add(ItemEntity(item))
//                                }
//                                mapImpassableList.remove(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))
                            }
                            is InteractionType.DOOR -> {
                                action.interaction.act()
                                if (action.entity.get(MapParams::class)?.allowCharacterOnTop == true)
                                    ChunkManager.characterMethods.removeImpassable(Position(Coord.get(
                                        character.getSuper(Ai::class)!!.targetCoords!!.first,
                                        character.getSuper(Ai::class)!!.targetCoords!!.second),
                                        character.get(Position::class)!!.chunk))
                                else
                                    ChunkManager.characterMethods.addImpassable(Position(Coord.get(
                                        character.getSuper(Ai::class)!!.targetCoords!!.first,
                                        character.getSuper(Ai::class)!!.targetCoords!!.second),
                                        character.get(Position::class)!!.chunk))

                                character.getSuper(Ai::class)!!.updateFov()
                                Player.call(VisionChangedCallable::class)
                            }
                            else -> {
                                action.interaction.act()
                            }
                        }
                        Player.getSuper(Ai::class)!!.targetCoords = null
                    }
                    // TODO ECS ITEM
//                    is Action.ITEM -> {
//                        ActorVisuals.showItemUsed(character, action.item)
//
//                        if (action.item is CausesEvents) {
//                            for (wrapper in action.item.eventWrappers) {
//                                if (wrapper.event.has("character"))
//                                    wrapper.event.set("character", action.character)
//
//                                when (wrapper) {
//                                    is TimedEvent -> {
//                                        eventArray.startEvent(
//                                            CharacterEvent(
//                                                action.character, wrapper, turn
//                                        ))
//                                    }
//                                    is OnOffEvent -> {
//                                        eventArray.startEvent(
//                                            CharacterEvent(
//                                                action.character, wrapper, turn
//                                            ))
//                                    }
//                                    is CharacterEvent -> {
//                                        eventArray.startEvent(wrapper)
//                                    }
//                                }
//                            }
//                            updateBatch.addFirst(Action.EVENT)
//                        }
//                        if (action.item is CausesCooldown && action.item.cooldownType != CooldownType.NONE) {
//                            eventArray.startEvent(
//                                CharacterEvent(
//                                action.character, turn, action.item.cooldownLength, 1,
//                                EventCooldown(action.character, action.item.cooldownType, action.item.cooldownLength)
//                            )
//                            )
//                        }
//                    }
                    is Action.SKILL -> {
                        when (action.skill) {
                            is Skill.ActiveSkill -> {
                                action.skill.use()
                            }
                            is Skill.ActiveSkillCharacter -> {
                                action.skill.use(action.target!!)
                            }
                            is Skill.ActiveSkillTile -> {
                                action.skill.use(action.tile!!)
                            }
                            is Skill.ActiveSkillArea -> {
                                action.skill.use(action.tile!!)
                            }
                            is Skill.PassiveSkill -> {
                                throw Exception("Skill cannot be used")
                            }
                        }
                        if (action.skill.manaCost != null) {
                            val multiplier = Player.get(CharacterTags::class)!!.getTag(CharacterTag.ReduceCooldown::class)?.reducePercent ?: 1f
                            Player.get(DefensiveStats::class)!!.mp -= action.skill.manaCost!! * multiplier
                        }
                    }

                    is Action.WAIT -> {
                        println("passing turn")
                    }
                    is Action.EVENT -> {
                        println("caused an event")
                    }

                    else -> {}
                }
                playerAction = false
                // TODO ECS Character info panel
//                characterArray.forEach {it.updateTurnBar()}
//                characterArray.forEach { println("${it.name}, ${it.turn}") }
//                println()
            } else {
                // initialize the ai if it's 10 tiles or less from the player
                if (character has EnemyAi::class)
                    character.get(EnemyAi::class)!!.decide()
                else
                    character.getSuper(Ai::class)!!.action = Action.WAIT

                var action: Action = character.getSuper(Ai::class)!!.useAction()
                when (action) {
                    is Action.MOVE -> {
                        if (updateBatch.firstOrNull() is Action.MOVE) { // Some character has moved in the meantime, so the movement map should be updated
                            val prevCoord = character.getSuper(Ai::class)!!.moveList.lastOrNull() ?: Coord.get(action.x, action.y)

                            character.getSuper(Ai::class)!!.setMoveList(prevCoord.x, prevCoord.y, true)
                            val coord = character.getSuper(Ai::class)!!.getMove()
                            action = Action.MOVE(coord.x, coord.y)
                        }

                        character.get(Position::class)!!.moveCharacter(Position(action.x, action.y, currentChunk))
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                        character.getSuper(Ai::class)!!.updateFov()
                    }
                    is Action.ATTACK -> {
                        character.get(OffensiveStats::class)!!.attack(Position(action.x, action.y, currentChunk))
                    }
                    is Action.SKILL -> {
                        println(character.name + " used a skill")
                        if (action.skill.manaCost != null) {
                            val multiplier = character.get(CharacterTags::class)?.getTag(CharacterTag.ReduceCooldown::class)?.reducePercent ?: 1f
                            character.get(DefensiveStats::class)!!.mp -= action.skill.manaCost!! * multiplier
                        }
                    }
                    is Action.INTERACTION -> {
                        println(character.name + " interacted with ${action.entity.name}")
                    }
                    is Action.ITEM -> {
                        // TODO ECS ITEM
//                        ActorVisuals.showItemUsed(character, action.item)
                        println(character.name + " used an item")
                    }
                    is Action.WAIT -> {
//                        println(character.name + " is passing turn")
                    }
                    is Action.NOTHING -> {
                        println(character.name + " did nothing")
                    }
                    is Action.EVENT -> {
                        println("caused an event")
                    }
                }
                // TODO ECS Character info panel
//                character.updateTurnBar(false)
            }
            characterArray.move(character)
            while (updateBatch.firstOrNull() is Action.EVENT) {
                println("Executing event from updatebatch")
                executeEvent()
                updateBatch.removeFirst()
            }

            // events
            while (eventArray.isNotEmpty() && eventArray.get(turn) != null) {
                executeEvent()
            }
            // global events
            while (globalEventArray.isNotEmpty() && globalEventArray.get(turn) != null) {
                val globalEvent = globalEventArray[0]
            }
        }
        // events
        while (eventArray.isNotEmpty() && eventArray.get(turn) != null) {
            executeEvent()
        }
        // global events
        while (globalEventArray.isNotEmpty() && globalEventArray.get(turn) != null) {
            val globalEvent = globalEventArray[0]
        }
        tick()
    }

    /**
     * Called from character class when enemy is killed.
     */
    private fun characterDied(character: Entity) {
        if (character == Player)
            return playerDied()

        // TODO ECS Attack levelling
//        Player.experience += character.experience
        val chunk = character.get(Position::class)!!.chunk
        chunk.characterMap[character.y][character.x] = null
        chunk.characterArray.remove(character)
        characterArray.remove(character)
        character.get(Texture::class)!!.textures.clear()

        // Drop its items
        // TODO ECS ITEM
//        if (character is HasDrops) {
//            character.dropItems().forEach {
//                currentLevel.map[character.yPos][character.xPos].add(ItemEntity(it))
//            }
//        }

        // TODO ECS Events
//        eventArray.remove(character)
    }

    private fun playerDied() {
        /** Exiting the app **/
        println("\n\n=======================================================================================================================================\n")
        println("Current score is: ${Player.get(com.neutrino.game.entities.characters.attributes.Level::class)!!.experience}")
        // TODO ECS Items
//        println("\tGold collected: ${Player.inventory.get("Gold")?.amount?:0}")
        println("\n=======================================================================================================================================\n\n")
        Gdx.app.exit()
        System.exit(0)

        characterArray.remove(Player)
    }

    private fun executeEvent() {
        try {
            val event = eventArray.get(turn)!!

            if (event.curRepeat >= event.executions) {
                event.event.stop()
                eventArray.stopEvent(event)
                return
            }

            event.event.start()
            event.turn += event.timeout
            event.curRepeat++

            eventArray.move(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setObservers() {
        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.EVENT
            override fun update(data: Any?): Boolean {
                if (data != null && data is EventWrapper) {
                    when (data) {
                        is CharacterEvent -> eventArray.startEvent(data)
                    }
                }
                return true
            }
        } )

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.CHARACTERDIED
            override fun update(data: Any?): Boolean {
                if (data != null && data is Entity)
                    characterDied(data)

                return true
            }
        })
    }

}