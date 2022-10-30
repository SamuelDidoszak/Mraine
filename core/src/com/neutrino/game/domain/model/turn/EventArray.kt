package com.neutrino.game.domain.model.turn

import com.neutrino.game.equalsDelta
import com.neutrino.game.lessThanDelta

class EventArray: ArrayList<Event>() {
    /** Adds the event both to this list and the character arrayList */
    fun startEvent(event: Event): Boolean {
        event.character.characterEventArray.add(event)
        return this.add(event)
    }

    /** Removes the event both from this list and the character arrayList */
    fun stopEvent(event: Event): Boolean {
        event.character.characterEventArray.remove(event)
        return this.remove(event)
    }

    override fun add(element: Event): Boolean {
        for (i in 0 until this.size) {
            if (element.turn.lessThanDelta(this[i].turn)) {
                this.add(i, element)
                return true
            }
        }
        this.add(this.size, element)
        return true
    }

    fun move(event: Event): Boolean {
        if (this.size == 1) return true
        return if(this.remove(event))
            this.add(event)
        else
            false
    }

    fun move(index: Int): Boolean {
        if (this.size == 1) return true
        val event =
            try {
                this.elementAt(index)
            } catch (e: IndexOutOfBoundsException) {
                return false
            }
        return move(event)
    }

    /**
     * Get event by turn
     * @return null if it's not event's turn
     */
    fun get(turn: Double): Event? {
        return if (this.elementAt(0).turn.equalsDelta(turn))
            this.elementAt(0)
        else
            null
    }

}