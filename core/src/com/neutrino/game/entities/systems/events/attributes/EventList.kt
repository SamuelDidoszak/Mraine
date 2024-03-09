package com.neutrino.game.entities.systems.events.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.events.TimedEvent

class EventList: Attribute() {

    val events: ArrayList<TimedEvent> = ArrayList()
}