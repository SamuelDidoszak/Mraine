package com.neutrino.game.entities.items.attributes.usable

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent

class EquipEvents(
    vararg initialEvents: TimedEvent
): Attribute() {

    private val events: ArrayList<TimedEvent> = ArrayList()
    private var equippedEntity: Entity? = null

    init {
        events.addAll(initialEvents)
    }

    override fun onEntityAttached() {
        entity.attach(object : OnItemEquipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                events.forEach {
                    if (it.event is CharacterEvents)
                        it.event.entity = data[0] as Entity
                    Events.addEvent(data[0] as Entity, it) }
                equippedEntity = data[0] as Entity
                return true
            }
        })
        entity.attach(object : OnItemUnequipped() {
            override fun call(entity: Entity, vararg data: Any?): Boolean {
                events.forEach {
                    if (it.event is CharacterEvents)
                        it.event.entity = data[0] as Entity
                    Events.remove(it) }
                equippedEntity = null
                return true
            }
        })
    }

    fun getEvents(): List<TimedEvent> = events

    fun addEvent(event: TimedEvent) {
        events.add(event)
        if (equippedEntity != null) {
            if (event.event is CharacterEvents)
                event.event.entity = equippedEntity!!
            Events.addEvent(event)
        }
    }

    fun removeEvent(event: TimedEvent) {
        events.remove(event)
        if (equippedEntity != null)
            Events.remove(event)
    }

    fun addAll(eventList: List<TimedEvent>) {
        eventList.forEach { addEvent(it) }
    }

    fun removeAll(eventList: List<TimedEvent>) {
        eventList.forEach { removeEvent(it) }
    }
}