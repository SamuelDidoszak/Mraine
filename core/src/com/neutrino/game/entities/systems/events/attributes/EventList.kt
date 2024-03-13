package com.neutrino.game.entities.systems.events.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.TimedEvent

class EventList: Attribute() {

    val events: ArrayList<TimedEvent> = ArrayList()

    fun hasCooldown(cooldown: Cooldown.Type): Boolean {
        return events.find { it.event is Cooldown && it.event.type == cooldown } != null
    }
}