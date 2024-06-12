package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.systems.skills.Skill
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
        object FOOD: Type() {
            override fun toString(): String = "Food"
            override fun equals(other: Any?): Boolean = other is FOOD
        }
        data class SKILL(val skill: Skill): Type(){
            override fun toString() = skill.name
            override fun equals(other: Any?): Boolean = (other as? SKILL)?.skill == skill
            override fun hashCode(): Int = skill.hashCode()
        }
        data class ITEM(val id: EntityId): Type() {
            override fun toString(): String = "Item ${Items.getName(id)}"
            override fun equals(other: Any?): Boolean = (other as? ITEM)?.id == id
            override fun hashCode(): Int = id
        }
        data class NAME(val name: String): Type() {
            override fun toString(): String = name
            override fun equals(other: Any?): Boolean = (other as? NAME)?.name == name
            override fun hashCode(): Int = name.hashCode()
        }
    }
}