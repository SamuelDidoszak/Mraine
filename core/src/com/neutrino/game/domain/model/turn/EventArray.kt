package com.neutrino.game.domain.model.turn

import com.neutrino.game.domain.model.event.wrappers.CharacterEvent
import com.neutrino.game.equalsDelta
import com.neutrino.game.lessThanDelta

class EventArray: ArrayList<CharacterEvent>() {
    /** Adds the event both to this list and the character arrayList */
    fun startEvent(characterEvent: CharacterEvent): Boolean {
        characterEvent.character.characterEventArray.add(characterEvent)
        return this.add(characterEvent)
    }

    /** Removes the event both from this list and the character arrayList */
    fun stopEvent(characterEvent: CharacterEvent): Boolean {
        characterEvent.character.characterEventArray.remove(characterEvent)
        return this.remove(characterEvent)
    }

    override fun add(element: CharacterEvent): Boolean {
        for (i in 0 until this.size) {
            if (element.turn.lessThanDelta(this[i].turn)) {
                this.add(i, element)
                return true
            }
        }
        this.add(this.size, element)
        return true
    }

    fun move(eventOld: CharacterEvent): Boolean {
        if (this.size == 1) return true
        return if(this.remove(eventOld))
            this.add(eventOld)
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
    fun get(turn: Double): CharacterEvent? {
        return if (this.elementAt(0).turn.equalsDelta(turn))
            this.elementAt(0)
        else
            null
    }
}