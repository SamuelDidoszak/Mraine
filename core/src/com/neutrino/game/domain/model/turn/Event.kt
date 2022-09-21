package com.neutrino.game.domain.model.turn

import com.neutrino.game.domain.model.characters.Character

sealed class Event(
    open var turn: Double,
    /** After how long is the next turn */
    open val speed: Double,
    /** How many times the event will occur */
    open val repeats: Int
) {
    var curRepeat: Int = 0
    data class HEAL(val character: Character, val isFood: Boolean, val power: Float,
                    override var turn: Double, override val speed: Double = 0.0, override val repeats: Int = 1): Event(turn, speed, repeats)
    data class COOLDOWN(val character: Character, val cooldownType: CooldownType,
                        override var turn: Double, val cooldownLength: Double): Event(turn, 0.0, 1) {init {
                            turn = turn + cooldownLength
                        }}
}
sealed class CooldownType {
    object FOOD: CooldownType()
    object HEAL: CooldownType()
    // TODO skills not yet implemented
    data class SKILL(val skill: Int)
}