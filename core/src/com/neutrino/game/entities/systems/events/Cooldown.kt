package com.neutrino.game.entities.systems.events

import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.entities.Entity
import com.neutrino.game.util.EntityId

class Cooldown(
    val entity: Entity,
    val type: Type,
    val length: Double
): Event {

    override fun apply() {

    }

    fun asTimedEvent(): TimedEvent = TimedEvent(this, length, 1)

    sealed class Type {
        object FOOD: Type()
        data class SKILL(val skill: Skill): Type()
        data class ITEM(val id: EntityId): Type()
        data class NAME(val name: String): Type()
    }
}