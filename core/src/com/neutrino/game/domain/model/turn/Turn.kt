package com.neutrino.game.domain.model.turn

import com.neutrino.game.Constants.IsSeeded
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.Constants.RunSpeed
import com.neutrino.game.Constants.Seed
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Action
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

    var updateBatch: Action = Action.NOTHING

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
    var eventList: MutableList<Event> = mutableListOf()

    /**
     * Stores time dependant global event information
     */
    val globalEventList: MutableList<Event> = mutableListOf()


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

            if (updateBatch == Action.MOVE(character.xPos, character.yPos)) {
                updateBatch = Action.NOTHING
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
                            if (updateBatch is Action.NOTHING) RunSpeed else MoveSpeed)
                        updateBatch = Action.MOVE(action.x, action.y)
                    }
                    is Action.ATTACK -> {
                        val clickedCharacter = characterArray.get(action.x, action.y)
                        clickedCharacter!!.getDamage(character)
                        // Enemy is killed
                        if (clickedCharacter.currentHp <= 0) {
                            Player.experience += clickedCharacter.experience
                            characterArray.remove(clickedCharacter)
                        }
                    }
                    is Action.PICKUP -> {
                        val topmostItem = currentLevel.getTopItem(action.x, action.y)!!
                        Player.addToEquipment(topmostItem)
                        Player.showPickedUpItem(topmostItem)
                        currentLevel.map.map[action.y][action.x].removeLast()
                    }

                    is Action.WAIT -> {
                        println("passing turn")
                    }
                    is Action.SKILL -> {
                        println("using skill")
                        TODO("skills not implemented")
                    }
                }
                playerAction = false
                charactersUseCases.updateTurnBars()
            } else {
                // initialize the ai if it's 30 tiles or less from the player
                if (abs(character.xPos - Player.xPos) <= 30 || abs(character.yPos - Player.yPos) <= 30)
                    character.ai.decide(Player.xPos, Player.yPos, dijkstraMap, charactersUseCases.getImpassable())
                else
                    character.ai.action = Action.WAIT

                var action: Action = character.ai.useAction()
                when (action) {
                    is Action.MOVE -> {
                        if (updateBatch is Action.MOVE) { // Some character has moved in the meantime, so the movement map should be updated
                            character.ai.setMoveList(character.ai.xTarget, character.ai.yTarget, dijkstraMap, charactersUseCases.getImpassable(), true)
                            val coord = character.ai.getMove()
                            action = Action.MOVE(coord.x, coord.y)
                        }

                        moveCharacter(character.xPos, character.yPos, action.x, action.y)
                        character.move(action.x, action.y)
                        updateBatch = Action.MOVE(action.x, action.y)
                    }
                    is Action.ATTACK -> {
                        val attackedCharacter = characterArray.get(action.x, action.y)
                        if (attackedCharacter == null) {
                            println("No character there")
                        } else {
                            attackedCharacter.getDamage(character)
                            if (attackedCharacter.currentHp <= 0.0)
                                characterArray.remove(attackedCharacter)
                        }
                    }
                    is Action.SKILL -> {
                        println(character.name + " used a skill")}
                    is Action.WAIT -> {
//                        println(character.name + " is passing turn")
                    }
                    is Action.NOTHING -> {
                        println(character.name + " did nothing")
                    }
                    is Action.PICKUP -> {}
                }
                character.updateTurnBar()
            }
            characterArray.move(character)
        }
        // events
        while (eventList.isNotEmpty() && eventList[0].turn == turn) {
            val event = eventList[0]
        }
        // global events
        while (globalEventList.isNotEmpty() && globalEventList[0].turn == turn) {
            val globalEvent = globalEventList[0]
        }
        tick()
    }

    private fun moveCharacter(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val characterToMove = characterMap[fromY][fromX]
        characterMap[fromY][fromX] = null
        characterMap[toY][toX] = characterToMove
    }

}