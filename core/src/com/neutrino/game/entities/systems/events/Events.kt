package com.neutrino.game.entities.systems.events

import com.neutrino.game.domain.model.characters.Player.roundToDecimalPlaces
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.events.attributes.EventList
import com.neutrino.game.map.chunk.Chunk

object Events {

    private val events = EventArray()
    private val globalEvents = EventArray()

    fun execute() {
        while (events.isNotEmpty() && events.get(Turn.turn) != null) {
            executeEvent(events)
        }
        while (globalEvents.isNotEmpty() && globalEvents.get(Turn.turn) != null) {
            executeEvent(globalEvents)
        }
    }

    private fun executeEvent(array: EventArray) {
        try {
            val eventElement = array.get(Turn.turn)!!
            val event = eventElement.second

            if (event.executions == 0) {
                event.event.stop()
                if (eventElement.first is EventArray.Identity.Entity)
                    detachFromEntity((eventElement.first as EventArray.Identity.Entity).entity, event)
                array.remove(eventElement)
                return
            }

            event.event.apply()
            event.turn = (event.turn!! + event.refreshTime).roundToDecimalPlaces(1000)
            event.executions -= 1

            array.move(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addEvent(timedEvent: TimedEvent) {
        events.add(null to timedEvent)
    }
    fun addEvent(entity: Entity, timedEvent: TimedEvent) {
        events.add(EventArray.Identity.Entity(entity) to timedEvent)
        if (entity hasNot EventList::class)
            entity.addAttribute(EventList())
        entity.get(EventList::class)!!.events.add(timedEvent)
    }
    fun addEvent(chunk: Chunk, timedEvent: TimedEvent) {
        events.add(EventArray.Identity.Chunk(chunk) to timedEvent)
    }
    fun addGlobalEvent(timedEvent: TimedEvent, chunk: Chunk? = null) {
        if (chunk == null)
            globalEvents.add(null to timedEvent)
        else
            globalEvents.add(EventArray.Identity.Chunk(chunk) to timedEvent)
    }

    fun remove(event: TimedEvent) {
        events.remove(event)
    }

    fun remove(entity: Entity) {
        events.remove(entity)
        globalEvents.remove(entity)
    }

    /** Remove all events connected to this chunk */
    fun remove(chunk: Chunk) {
        chunk.events.clear()
        val iterator = events.iterator()
        while (iterator.hasNext()) {
            if (iterator.next().first?.getChunk()?.id == chunk.id) {
                chunk.events.add(iterator.next())
                iterator.remove()
            }
        }

        chunk.globalEvents.clear()
        val iterator2 = globalEvents.iterator()
        while (iterator2.hasNext()) {
            if (iterator2.next().first?.getChunk()?.id == chunk.id) {
                chunk.globalEvents.add(iterator2.next())
                iterator2.remove()
            }
        }
    }

    private fun detachFromEntity(entity: Entity, event: TimedEvent) {
        entity.get(EventList::class)!!.events.remove(event)
        if (entity.get(EventList::class)!!.events.isEmpty())
            entity.removeAttribute(EventList::class)
    }
}