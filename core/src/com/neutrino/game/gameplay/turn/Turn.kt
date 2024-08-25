package com.neutrino.game.gameplay.turn

import com.badlogic.gdx.Gdx
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.items.attributes.usable.Use
import com.neutrino.game.entities.items.attributes.usable.UseOnEntity
import com.neutrino.game.entities.items.attributes.usable.UseOnPosition
import com.neutrino.game.entities.items.callables.UsedCallable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.attributes.Chest
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.entities.map_entities.attributes.PickUp
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.events.Event
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.util.visuals.Visuals
import com.neutrino.game.map.chunk.CharacterArray
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkCoords
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
                    character.get(Position::class)!!.y
                )
            ) {
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
                            is PickUp -> {
                                if (Player.get(Inventory::class)!!.add(action.entity)) {
                                    (Player as Character).setAnimation("crouch", "idle")
                                    GlobalData.notifyObservers(GlobalDataType.PICKUP, action.entity)
                                    Visuals.showPickedUpItem(Player, action.entity)
                                    val coords = Player.getSuper(Ai::class)!!.targetCoords
                                    currentChunk.map[coords!!.second][coords.first].removeLast()
                                } else Visuals.showText(Player, "Inventory is full")
                            }
                            is Chest -> {
                                (Player as Character).setAnimation("crouch", "idle")
                                action.interaction.interact()
                            }
                            is Door -> {
                                action.interaction.interact()
                                character.getSuper(Ai::class)!!.updateFov()
                                Player.call(VisionChangedCallable::class)
                            }
                        }
                        Player.getSuper(Ai::class)!!.targetCoords = null
                    }
                    // TODO ECS ITEM
                    is Action.ITEM -> {
                        (Player as Character).setAnimation("item", "idle")
                        Visuals.showItemUsed(character, action.item)

                        if (action.targetEntity != null) {
                            action.item.get(UseOnEntity::class)!!.use(action.targetEntity)
                            action.item.call(UsedCallable::class, action.targetEntity)
                        }
                        else if (action.targetPosition != null) {
                            action.item.get(UseOnPosition::class)!!.use(action.targetPosition)
                            action.item.call(UsedCallable::class, Player)
                        }
                        else
                            action.item.get(Use::class)!!.use()

                        updateBatch.addFirst(Action.EVENT)
                    }
                    is Action.SKILL -> {
                        when (action.skill) {
                            is Skill.ActiveSkill -> {
                                action.skill.use()
                            }
                            is Skill.ActiveSkillCharacter -> {
                                action.skill.use(action.data as Character)
                            }
                            is Skill.ActiveSkillEntity -> {
                                action.skill.use(action.data as Entity)
                            }
                            is Skill.ActiveSkillPosition -> {
                                action.skill.use(action.data as Position)
                            }
                            is Skill.ActiveSkillArea -> {
                                action.skill.use(action.data as Position)
                            }
                            is Skill.PassiveSkill -> {
                                throw Exception("Passive skill cannot be used")
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

                    is Action.WAITSKILL -> {
                        if (action.skill != null) {
                            val useStopEvent = object : Event {
                                override fun apply() {
                                    action.skill.useStart()
                                }

                                override fun stop() {
                                    action.skill.useStop()
                                }
                            }
                            Events.addEvent(TimedEvent(useStopEvent, 1.0, 1))
                        }
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
                        Visuals.showItemUsed(character, action.item)
                        println(character.name + " used an item")
                    }
                    is Action.WAIT -> {
//                        println(character.name + " is passing turn")
                    }
                    is Action.WAITSKILL -> {

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
                Events.execute()
                updateBatch.removeFirst()
            }

            Events.execute()
        }
        tick()
        Events.execute()
    }

    /**
     * Called from character class when enemy is killed.
     */
    private fun characterDied(character: Entity) {
        if (character == Player)
            return playerDied()

        val experience = character.get(Experience::class)?.experience
        if (experience != null)
            Player.get(Level::class)!!.addExp(experience)

        val chunk = character.get(Position::class)!!.chunk
        chunk.characterMap[character.y][character.x] = null
        chunk.characterArray.remove(character)
        characterArray.remove(character)
        Events.remove(character)
        (character as Character).setAnimation("death")
        character.addAction(com.neutrino.game.graphics.drawing.actions.Action.Sequence(
            com.neutrino.game.graphics.drawing.actions.Action.Delay(1f),
            com.neutrino.game.graphics.drawing.actions.Action.FadeOut(1f),
            com.neutrino.game.graphics.drawing.actions.Action.Custom {
                character.get(Texture::class)!!.textures.clear()
                character.getDrawables()?.forEach { it.detach() }
//            shaders.clear()
            }
        ))
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


    private fun setObservers() {

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