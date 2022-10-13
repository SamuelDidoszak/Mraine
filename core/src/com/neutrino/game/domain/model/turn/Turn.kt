package com.neutrino.game.domain.model.turn

import com.badlogic.gdx.Gdx
import com.neutrino.game.Constants.IsSeeded
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.Constants.RunSpeed
import com.neutrino.game.Constants.Seed
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HpBar
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.characters.CharactersUseCases
import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.Measurement
import squidpony.squidmath.GWTRNG
import kotlin.math.abs

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
        set(value) {
            field = value
            charactersUseCases = CharactersUseCases(characterArray)
        }

    lateinit var characterMap: List<MutableList<Character?>>
    lateinit var currentLevel: Level

    /**
     * List containing various short term events, often related to characters.
     * Incorporates cooldowns, buffs, etc.
     */
    var eventArray: EventArray = EventArray()

    /**
     * Stores time dependant global event information
     */
    val globalEventArray: EventArray = EventArray()


    var charactersUseCases: CharactersUseCases = CharactersUseCases(characterArray)

    private val seed = if (IsSeeded) GWTRNG(Seed) else GWTRNG()
    var dijkstraMap: DijkstraMap = DijkstraMap(seed)

    fun setLevel(level: Level) {
        characterArray = level.characterArray
        characterMap = level.characterMap
        currentLevel = level
        // terrain cost can be easily added by calling the initializeCost method.
        dijkstraMap.measurement = Measurement.EUCLIDEAN

        dijkstraMap.initialize(level.movementMap)
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
                        character.move(action.x, action.y,
                            if (updateBatch.firstOrNull() == null) RunSpeed else MoveSpeed)
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                    }
                    is Action.ATTACK -> {
                        val clickedCharacter = characterArray.get(action.x, action.y)!!
                        clickedCharacter.getDamage(character)
                        // Enemy is killed
                        if (clickedCharacter.hp <= 0) {
                            Player.experience += clickedCharacter.experience
                            characterArray.remove(clickedCharacter)
                            // Drop its items
                            clickedCharacter.dropItems().forEach {
                                currentLevel.map.map[clickedCharacter.yPos][clickedCharacter.xPos].add(ItemEntity(it))
                            }
                        }
                    }
                    is Action.PICKUP -> {
                        val topmostItem = currentLevel.getTopItem(action.x, action.y)
                        // TODO temporary solution
                        if (topmostItem != null) {
                            if (Player.addToInventory(topmostItem)) {
                                Player.showPickedUpItem(topmostItem)
                                currentLevel.map.map[action.y][action.x].removeLast()
                            } else {
                                println("Inventory is full!")
                            }
                        }
                    }
                    is Action.ITEM -> {
                        character.showItemUsed(action.item)
                        when(action.item) {
                            is ItemType.EDIBLE -> {
                                val event = action.item.use(action.character, turn)
                                eventArray.startEvent(event)
                                // TODO change the cooldown type if more edible effects are added
                                val cooldownType = if (action.item.isFood) CooldownType.FOOD else CooldownType.HEAL
                                val cooldown = Event.COOLDOWN(action.character, cooldownType, turn, action.item.getEffectLength())
                                eventArray.startEvent(cooldown)
                                updateBatch.addFirst(Action.EVENT)
                            }
                            is ItemType.SCROLL -> {
                                when (action.item) {
                                    is ItemType.SCROLL.STAT -> {
                                        val event = action.item.use(action.character, turn)
                                        eventArray.startEvent(event)
                                        if (action.item.causesCooldown >= 0) {
                                            val cooldown = Event.COOLDOWN(Player, CooldownType.ITEM(action.item.name), turn, action.item.getEffectLength())
                                            eventArray.startEvent(cooldown)
                                            updateBatch.addFirst(Action.EVENT)
                                        }
                                    }
                                    else -> {}
                                }


                            }
                        }
                    }

                    is Action.WAIT -> {
                        println("passing turn")
                    }
                    is Action.SKILL -> {
                        println("using skill")
                    }
                    is Action.EVENT -> {
                        println("caused an event")
                    }
                }
                playerAction = false
                charactersUseCases.updateTurnBars()
            } else {
                // initialize the ai if it's 10 tiles or less from the player

                if (abs(character.xPos - Player.xPos) <= 10 && abs(character.yPos - Player.yPos) <= 10)
                    character.ai.decide(Player.xPos, Player.yPos, dijkstraMap, charactersUseCases.getImpassable())
                else
                    character.ai.action = Action.WAIT

                var action: Action = character.ai.useAction()
                when (action) {
                    is Action.MOVE -> {
                        if (updateBatch.firstOrNull() is Action.MOVE) { // Some character has moved in the meantime, so the movement map should be updated
                            character.ai.setMoveList(character.ai.xTarget, character.ai.yTarget, dijkstraMap, charactersUseCases.getImpassable(), true)
                            val coord = character.ai.getMove()
                            action = Action.MOVE(coord.x, coord.y)
                        }

                        moveCharacter(character.xPos, character.yPos, action.x, action.y)
                        character.move(action.x, action.y)
                        setMovementUpdateBatch(Action.MOVE(action.x, action.y))
                    }
                    is Action.ATTACK -> {
                        val attackedCharacter = characterArray.get(action.x, action.y)
                        if (attackedCharacter == null) {
                            println("No character there")
                        } else {
                            attackedCharacter.getDamage(character)
                            if (attackedCharacter.hp <= 0.0) {
                                /** Exiting the app **/
                                if (attackedCharacter is Player) {
                                    println("\n\n=======================================================================================================================================\n")
                                    println("Current score is: ${Player.experience}")
                                    println("\tGold collected: ${Player.inventory.get("Gold")?.amount}")
                                    println("\n=======================================================================================================================================\n\n")
                                    Gdx.app.exit()
                                    System.exit(0)
                                }
                                characterArray.remove(attackedCharacter)
                            }
                        }
                    }
                    is Action.SKILL -> {
                        println(character.name + " used a skill")}
                    is Action.ITEM -> {
                        character.showItemUsed(action.item)
                        println(character.name + " used an item")
                    }
                    is Action.WAIT -> {
//                        println(character.name + " is passing turn")
                    }
                    is Action.NOTHING -> {
                        println(character.name + " did nothing")
                    }
                    is Action.PICKUP -> {}
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

    /** Executes the event happening at current turn */
    private fun executeEvent() {
        try {
            val event = eventArray.get(turn)!!
            when (event) {
                is Event.HEAL -> {
                    event.character.hp += event.power
                    if (event.character.hp > event.character.hpMax)
                        event.character.hp = event.character.hpMax
                    event.character.findActor<HpBar>("hpBar").update(event.character.hp)
                    event.turn += event.speed
                    event.curRepeat++
                    if (event.curRepeat < event.repeats)
                        eventArray.move(0)
                    else {
                        eventArray.stopEvent(event)
                    }
                }
                is Event.COOLDOWN -> {
                    eventArray.removeAt(0)
                    event.character.eventArray.remove(event)
                }
                is Event.MODIFYSTAT -> {
                    val field = event.character.javaClass.declaredFields.first {it.name == event.statName}
                    field.trySetAccessible()
                    val fieldValue = field.get(event.character)

                    val multiplier: Int
                    if (event.curRepeat < event.repeats) {
                        event.turn += event.speed
                        event.curRepeat++
                        multiplier = 1
                    } else {
                        multiplier = -1 * event.repeats
                    }

                    when (field.type.toString()) {
                        "float" -> {
                            field.set(event.character, (fieldValue as Float) + multiplier * (event.power as Float))
                        }
                        "double" -> {
                            field.set(event.character, (fieldValue as Double) + multiplier * (event.power as Double))
                        }
                        "int" -> {
                            field.set(event.character, (fieldValue as Int) + multiplier * (event.power as Int))
                        }
                        else -> {
                            throw Exception("Field type is not supported")
                        }
                    }

                    if (multiplier != 1)
                        eventArray.stopEvent(event)
                }
                is Event.STUN -> {
                    event.character.turn += event.stunLength
                    eventArray.stopEvent(event)
                }
                // damage
                is Event.DAMAGE -> {
                    event.character.getDamage(event.power, event.damageType)
                    // Character is killed
                    if (event.character.hp <= 0) {
                        Player.experience += event.character.experience
                        characterArray.remove(event.character)
                        // Drop its items
                        event.character.dropItems().forEach {
                            currentLevel.map.map[event.character.yPos][event.character.xPos].add(ItemEntity(it))
                        }
                    }
                }
                else -> {println("Event ${event::class} not yet implemented")}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveCharacter(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val characterToMove = characterMap[fromY][fromX]
        characterMap[fromY][fromX] = null
        characterMap[toY][toX] = characterToMove
    }

}