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
                    override var turn: Double, val cooldownLength: Double): Event(turn, 0.0, 1) {
                        init {
                            turn = turn + cooldownLength
                        }}

    // Statuses
    data class STUN(val character: Character, override var turn: Double, val cooldownLength: Double): Event(turn, 0.0, 1) {
        init {
            turn = turn + cooldownLength
        }}
    data class POISON(val character: Character, val power: Float,
                      override var turn: Double, override val speed: Double = 0.0, override val repeats: Int = 1): Event(turn, speed, repeats)
    data class BURN(val character: Character, val power: Float,
                    override var turn: Double, override val speed: Double = 0.0, override val repeats: Int = 1): Event(turn, speed, repeats)
    data class BLEED(val character: Character, val power: Float,
                    override var turn: Double, override val speed: Double = 0.0, override val repeats: Int = 1): Event(turn, speed, repeats)
    data class MODIFYSTAT(val character: Character, val statName: String, val power: Float,
                          override var turn: Double, override val speed: Double, override val repeats: Int = 1): Event(turn, speed, repeats)
    data class MODIFYSPEED(val character: Character, val stat: Double, val power: Double,
                     override var turn: Double, val cooldownLength: Double): Event(turn, 0.0, 1) {
        init {
            turn = turn + cooldownLength
        }}

}
sealed class CooldownType {
    object FOOD: CooldownType()
    object HEAL: CooldownType()
    // TODO skills not yet implemented
    data class SKILL(val skill: Int): CooldownType()
    /** Cooldown for a specific item */
    data class ITEM(val itemName: String): CooldownType()
}