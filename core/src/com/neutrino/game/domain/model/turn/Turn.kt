package com.neutrino.game.domain.model.turn

import com.badlogic.gdx.Gdx
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.ActorVisuals
import com.neutrino.game.domain.model.characters.utility.EnemyAi
import com.neutrino.game.domain.model.characters.utility.Fov
import com.neutrino.game.domain.model.characters.utility.HasDrops
import com.neutrino.game.domain.model.entities.DungeonStairsDown
import com.neutrino.game.domain.model.entities.DungeonStairsUp
import com.neutrino.game.domain.model.entities.utility.Container
import com.neutrino.game.domain.model.entities.utility.Destructable
import com.neutrino.game.domain.model.entities.utility.Interaction
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.items.utility.HasProjectile
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.event.CausesEvents
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventCooldown
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.use_case.characters.CharactersUseCases
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.domain.use_case.level.LevelUseCases
import com.neutrino.game.has
import com.neutrino.game.lessThanDelta
import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.Measurement
import squidpony.squidmath.Coord
import squidpony.squidmath.GWTRNG

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
    var characterArray: CharacterArray = CharacterArray(Player)

    lateinit var characterMap: List<MutableList<Character?>>
    lateinit var currentLevel: Level
    lateinit var fov: Fov

    /**
     * List containing various short term events, often related to characters.
     * Incorporates cooldowns, buffs, etc.
     */
    var eventArray: EventArray = EventArray()

    /**
     * Stores time dependant global event information
     */
    val globalEventArray: EventArray = EventArray()

    lateinit var levelUseCases: LevelUseCases
    var charactersUseCases: CharactersUseCases = CharactersUseCases(characterArray)

    var dijkstraMap: DijkstraMap = DijkstraMap(GWTRNG())

    var mapImpassableList: ArrayList<Coord> = ArrayList()

    fun unsetLevel() {
        currentLevel.characterArray.clear()
        currentLevel.characterArray.addAll(characterArray)
        currentLevel.characterArray.remove(Player)
    }

    fun setLevel(level: Level) {
        currentLevel = level
        characterArray = level.characterArray
        charactersUseCases = CharactersUseCases(characterArray)
        characterMap = level.characterMap
        levelUseCases = LevelUseCases(level)
        mapImpassableList = levelUseCases.getImpassable() as ArrayList<Coord>
        // terrain cost can be easily added by calling the initializeCost method.
        dijkstraMap.measurement = Measurement.EUCLIDEAN

        dijkstraMap.initialize(level.movementMap)

        fov = Fov(level.map)
        for (character in characterArray) {
            fov.updateFov(character.xPos, character.yPos, character.ai.fov, character.viewDistance)
        }

        GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
    }

    fun makeTurn() {
        // character actions
        while (characterArray.get(turn) != null) {
            val character = characterArray.get(turn)!!
            playerAction = character == Player

            if (updateBatch.firstOrNull() == Action.MOVE(character.xPos, character.yPos)) {
                updateBatch.removeFirst()
                println("resetting update batch")
            }

            // Player actions
            if (playerAction) {
                // Makes the player action or returns if Action.NOTHING
                val action: Action = character.ai.useAction()
                when (action) {
                    is Action.NOTHING -> return
                    is Action.MOVE -> {
                        moveCharacter(character.xPos, character.yPos, action.x, action.y)
                        fov.updateFov(action.x, action.y, Player.ai.fov, Player.viewDistance)
                        character.move(action.x, action.y)
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                        if (currentLevel.map[action.y][action.x] has DungeonStairsDown::class)
                            GlobalData.notifyObservers(GlobalDataType.LEVELCHANGED, LevelChunkCoords(
                                currentLevel.levelChunkCoords.x,
                                currentLevel.levelChunkCoords.y,
                                currentLevel.levelChunkCoords.z - 1
                            ))
                        if (currentLevel.map[action.y][action.x] has DungeonStairsUp::class)
                            GlobalData.notifyObservers(GlobalDataType.LEVELCHANGED, LevelChunkCoords(
                                currentLevel.levelChunkCoords.x,
                                currentLevel.levelChunkCoords.y,
                                currentLevel.levelChunkCoords.z + 1
                            ))
                    }
                    is Action.ATTACK -> {
                        val clickedCharacter = characterArray.get(action.x, action.y)!!
                        Player.primaryAttack.attack(Player, Coord.get(action.x, action.y))
                    }
                    is Action.INTERACTION -> {
                        // Entity position(x, y) can be derived from ai.entityTargetCoords
                        when (action.interaction) {
                            is Interaction.ITEM -> {
                                val item = (action.entity as ItemEntity).item
                                if (Player.addToInventory(item)) {
                                    ActorVisuals.showPickedUpItem(Player, item)
                                    currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].removeLast()
                                } else {
                                    println("Inventory is full!")
                                }
                            }
                            is Interaction.DESTROY -> {
                                val entity = (action.entity as Destructable)
                                if (Player.equipment.getEquipped(EquipmentType.RHAND) is HasProjectile)
                                    (Player.equipment.getEquipped(EquipmentType.RHAND) as HasProjectile)
                                        .shoot(Player.xPos, Player.yPos, Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second, Player.parent.parent)

                                entity.entityHp -= character.damage
                                if (entity.entityHp.lessThanDelta(0f)) {
                                    val items = entity.destroy()
                                    if (items != null) {
                                        for (item in items) {
                                            currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].add(ItemEntity(item))
                                        }
                                    }
                                    mapImpassableList.remove(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))
                                }
                            }
                            is Interaction.OPEN -> {
                                currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].remove(action.entity)
                                for (item in (action.entity as Container).itemList) {
                                    currentLevel.map[Player.ai.entityTargetCoords!!.second][Player.ai.entityTargetCoords!!.first].add(ItemEntity(item))
                                }
                                mapImpassableList.remove(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))
                            }
                            is Interaction.DOOR -> {
                                action.interaction.act()
                                if (action.entity.allowCharacterOnTop)
                                    mapImpassableList.remove(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))
                                else
                                    mapImpassableList.add(Coord.get(Player.ai.entityTargetCoords!!.first, Player.ai.entityTargetCoords!!.second))

                                fov.updateFov(Player.xPos, Player.yPos, Player.ai.fov, Player.viewDistance)
                                GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
                            }
                            else -> {
                                action.interaction.act()
                            }
                        }
                        Player.ai.entityTargetCoords = null
                    }
                    is Action.ITEM -> {
                        ActorVisuals.showItemUsed(character, action.item)

                        if (action.item is CausesEvents) {
                            for (wrapper in action.item.eventWrappers) {
                                if (wrapper.event.has("character"))
                                    wrapper.event.set("character", action.character)

                                when (wrapper) {
                                    is TimedEvent -> {
                                        eventArray.startEvent(
                                            CharacterEvent(
                                                action.character, wrapper, turn
                                        ))
                                    }
                                    is OnOffEvent -> {
                                        eventArray.startEvent(
                                            CharacterEvent(
                                                action.character, wrapper, turn
                                            ))
                                    }
                                    is CharacterEvent -> {
                                        eventArray.startEvent(wrapper)
                                    }
                                }
                            }
                            updateBatch.addFirst(Action.EVENT)
                        }
                        if (action.item is CausesCooldown && action.item.cooldownType != CooldownType.NONE) {
                            eventArray.startEvent(
                                CharacterEvent(
                                action.character, turn, action.item.cooldownLength, 1,
                                EventCooldown(action.character, action.item.cooldownType, action.item.cooldownLength)
                            )
                            )
                        }
                    }
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
                            val multiplier = Player.getTag(CharacterTag.ReduceCooldown::class)?.reducePercent ?: 1f
                            Player.mp -= action.skill.manaCost!! * multiplier
                        }
                    }

                    is Action.WAIT -> {
                        println("passing turn")
                    }
                    is Action.EVENT -> {
                        println("caused an event")
                    }
                }
                playerAction = false
//                charactersUseCases.updateTurnBars()
//                characterArray.forEach { println("${it.name}, ${it.turn}") }
//                println()
            } else {
                // initialize the ai if it's 10 tiles or less from the player
                if (character.ai is EnemyAi)
                    (character.ai as EnemyAi).decide()
                else
                    character.ai.action = Action.WAIT

                var action: Action = character.ai.useAction()
                when (action) {
                    is Action.MOVE -> {
                        if (updateBatch.firstOrNull() is Action.MOVE) { // Some character has moved in the meantime, so the movement map should be updated
                            val prevCoord = character.ai.moveList.lastOrNull() ?: Coord.get(action.x, action.y)
                            character.ai.setMoveList(prevCoord.x, prevCoord.y, dijkstraMap, mapImpassableList.plus(charactersUseCases.getImpassable()), true)
                            val coord = character.ai.getMove()
                            action = Action.MOVE(coord.x, coord.y)
                        }

                        moveCharacter(character.xPos, character.yPos, action.x, action.y)
                        character.move(action.x, action.y)
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                        fov.updateFov(character.xPos, character.yPos, character.ai.fov, character.viewDistance)
                    }
                    is Action.ATTACK -> {
                        val attackedCharacter = characterArray.get(action.x, action.y)
                        if (attackedCharacter == null) {
                            println("No character there")
                        } else {
                            character.primaryAttack.attack(character, Coord.get(action.x, action.y))
                        }
                    }
                    is Action.SKILL -> {
                        println(character.name + " used a skill")
                        if (action.skill.manaCost != null) {
                            val multiplier = character.getTag(CharacterTag.ReduceCooldown::class)?.reducePercent ?: 1f
                            character.mp -= action.skill.manaCost!! * multiplier
                        }
                    }
                    is Action.INTERACTION -> {
                        println(character.name + " interacted with ${action.entity.name}")
                    }
                    is Action.ITEM -> {
                        ActorVisuals.showItemUsed(character, action.item)
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
                character.updateTurnBar(false)
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
    private fun characterDied(character: Character) {
        if (character is Player)
            return playerDied()

        Player.experience += character.experience
        characterArray.remove(character)
        // Drop its items
        if (character is HasDrops) {
            character.dropItems().forEach {
                currentLevel.map[character.yPos][character.xPos].add(ItemEntity(it))
            }
        }

        eventArray.remove(character)
    }

    private fun playerDied() {
        /** Exiting the app **/
        println("\n\n=======================================================================================================================================\n")
        println("Current score is: ${Player.experience}")
        println("\tGold collected: ${Player.inventory.get("Gold")?.amount?:0}")
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

    private fun moveCharacter(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val characterToMove = characterMap[fromY][fromX]
        characterMap[fromY][fromX] = null
        characterMap[toY][toX] = characterToMove
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
                if (data != null && data is Character)
                    characterDied(data)

                return true
            }
        })
    }

}