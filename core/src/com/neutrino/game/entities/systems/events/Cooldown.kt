package com.neutrino.game.entities.systems.events

import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
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
            override fun toString(): String {
                return "Food"
        } }
        data class SKILL(val skill: Skill): Type(){
            override fun toString(): String {
                return skill.name
        } }
        data class ITEM(val id: EntityId): Type() {
            override fun toString(): String {
                return "Item ${Items.getName(id)}"
        } }
        data class NAME(val name: String): Type() {
            override fun toString(): String {
                return name
        } }
    }
}