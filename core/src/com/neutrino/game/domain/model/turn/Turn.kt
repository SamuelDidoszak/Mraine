package com.neutrino.game.domain.model.turn

import com.neutrino.game.Constants.IsSeeded
import com.neutrino.game.Constants.Seed
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

    var playerAction: Boolean = true

    /**
     * A list of current level characters. Should be changed / added to when entering a new Level
     */
    var characterArray: CharacterArray = CharacterArray(Player)
        set(value) {
            field = value
            charactersUseCases = CharactersUseCases(characterArray)
        }

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
        // terrain cost can be easily added by calling the initializeCost method.
        dijkstraMap.measurement = Measurement.EUCLIDEAN

        dijkstraMap.initialize(level.movementMap)
    }

    fun makeTurn() {
        // character actions
        while (characterArray.get(turn) != null) {
            val character = characterArray.get(turn)
            playerAction = character == Player

            // Player actions
            if (playerAction) {
                // Makes the player action or returns if Action.NOTHING
                val action: Action = character!!.ai.useAction()
                when (action) {
                    is Action.NOTHING -> return
                    is Action.MOVE -> {
                        character.move(action.x, action.y)
                        characterArray.move(character)
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
                    is Action.WAIT -> {
                        println("passing turn")
                    }
                    is Action.SKILL -> {
                        println("using skill")
                        TODO("skills not implemented")
                    }
                }
            } else {
                // initialize the ai if it's 30 tiles or less from the player
                if (abs(character!!.xPos - Player.xPos) <= 30 || abs(character.yPos - Player.yPos) <= 30)
                    character.ai.decide(Player.xPos, Player.yPos, dijkstraMap, charactersUseCases.getImpassable())
                else
                    character.ai.action = Action.WAIT

                val action: Action = character.ai.useAction()
                when (action) {
                    is Action.MOVE -> character.move(action.x, action.y)
                    is Action.ATTACK -> {
                        val attackedCharacter = characterArray.get(action.x, action.y)
                        if (attackedCharacter == null) {
                            println("No character there")
                        } else {
                            attackedCharacter.getDamage(character)
                            if (attackedCharacter.currentHp <= 0.0) {
                                characterArray.remove(attackedCharacter)
                            }
                        }
                    }
                    is Action.SKILL -> {
                        println(character.name + " used a skill")}
                    is Action.WAIT -> {
                        println(character.name + " is passing turn")
                    }
                    is Action.NOTHING -> {
                        println(character.name + " did nothing")
                    }
                }
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

}