package com.neutrino.game.entities.systems.events.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.TimedEvent

class EventList: Attribute() {

    val events: ArrayList<TimedEvent> = object : ArrayList<TimedEvent>() {
        override fun add(element: TimedEvent): Boolean {
            if (element.event is Cooldown && element.event.type is Cooldown.Type.SKILL && element.event.type.skill.manaCost == null)
                skillsOnCooldown++
            return super.add(element)
        }

        override fun remove(element: TimedEvent): Boolean {
            if (element.event is Cooldown && element.event.type is Cooldown.Type.SKILL && element.event.type.skill.manaCost == null)
                skillsOnCooldown--
            return super.remove(element)
        }
    }
    var skillsOnCooldown: Int = 0
        private set

    fun hasCooldown(cooldown: Cooldown.Type): Boolean {
        return events.find { it.event is Cooldown && it.event.type == cooldown } != null
    }
}