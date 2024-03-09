package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.events.Event

class EventList: Attribute() {
    val events: ArrayList<Event> = ArrayList()
}