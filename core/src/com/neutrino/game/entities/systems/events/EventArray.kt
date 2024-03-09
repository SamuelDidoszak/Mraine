package com.neutrino.game.entities.systems.events

import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.systems.events.attributes.EventList
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.util.equalsDelta
import com.neutrino.game.util.lessThanDelta

class EventArray: ArrayList<Pair<EventArray.Identity?, TimedEvent>>() {

    override fun add(element: Pair<Identity?, TimedEvent>): Boolean {
        if (element.second.turn == null)
            element.second.turn = Turn.turn
        for (i in 0 until this.size) {
            if (element.second.turn!!.lessThanDelta(this[i].second.turn!!)) {
                this.add(i, element)
                return true
            }
        }
        this.add(this.size, element)
        return true
    }

    fun move(index: Int): Boolean {
        if (this.size == 1) return true
        if (index > this.size) return false
        return move(this.elementAt(index))
    }

    fun move(eventOld: Pair<Identity?, TimedEvent>): Boolean {
        if (this.size == 1) return true
        return if(this.remove(eventOld))
            this.add(eventOld)
        else
            false
    }

    /**
     * Get event by turn
     * @return null if it's not event's turn
     */
    fun get(turn: Double): Pair<Identity?, TimedEvent>? {
        return if (this.elementAt(0).second.turn!!.equalsDelta(turn))
            this.elementAt(0)
        else
            null
    }

    /** Remove all events connected to the entity */
    fun remove(entity: Entity) {
        val events = entity.get(EventList::class)?.events ?: return
        val iterator = this.iterator()
        while (events.isNotEmpty() && iterator.hasNext()) {
            val event = iterator.next()
            if (event.first is Identity.Entity && (event.first as Identity.Entity).entity == entity) {
                iterator.remove()
                events.remove(event.second)
            }
        }
    }

    abstract class Identity {
        abstract fun getChunk(): com.neutrino.game.map.chunk.Chunk

        data class Entity(val entity: com.neutrino.game.entities.Entity): Identity() {
            override fun getChunk(): com.neutrino.game.map.chunk.Chunk {
                return entity.get(Position::class)!!.chunk
            }
        }

        data class Chunk(val chunk: com.neutrino.game.map.chunk.Chunk): Identity() {
            override fun getChunk(): com.neutrino.game.map.chunk.Chunk = chunk
        }
    }
}