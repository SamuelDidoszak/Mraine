package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.systems.events.Event
import com.neutrino.game.entities.systems.events.TimedEvent
import kotlin.reflect.KClass

class UseEvents(
    vararg events: TimedEvent
): Attribute() {
    val events: ArrayList<TimedEvent> = ArrayList()
    init {
        this.events.addAll(events)
    }

    fun <T: Event>get(event: KClass<T>): TimedEvent? = events.find { it.event::class == event }
    fun <T: Event>getEvent(event: KClass<T>): T? = events.find { it.event::class == event }?.event as? T
}