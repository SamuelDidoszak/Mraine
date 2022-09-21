package com.neutrino.game.domain.model.turn

import kotlin.math.abs

class EventArray(): ArrayList<Event>() {
    constructor(event: Event): this() {
        this.add(event)
    }
    private fun Double.equalsDelta(other: Double) = abs(this - other) <= 0.005
    private fun Double.lessThanDelta(other: Double) = (this - other) < -0.0000001

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