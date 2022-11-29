package com.neutrino.game.domain.model.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.event.Event
import com.neutrino.game.domain.model.event.Timed

class EventCooldown(
    val cooldownType: CooldownType,
    val cooldownLength: Double
): Event<Character>, Timed {
    override lateinit var data: Character
    override var dataAttached: Boolean = false

    override val turnDelay: Double = cooldownLength
    override val timeout: Double = 0.0
    override var executions: Int = 1
}

sealed class CooldownType {
    object FOOD: CooldownType()
    // TODO skills not yet implemented
    data class SKILL(val skill: Int): CooldownType()
    /** Cooldown for a specific item */
    data class ITEM(val itemName: String): CooldownType()
    object NONE: CooldownType()
}