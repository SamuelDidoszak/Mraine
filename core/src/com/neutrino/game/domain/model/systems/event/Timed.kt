package com.neutrino.game.domain.model.systems.event

interface Timed {
    /** Execution delay */
    val turnDelay: Double
    /** Length of delay before the next execution */
    val cooldown: Double
    /** How many times the event will occur */
    var executions: Int

    fun getEventLength(): Double {
        return executions * cooldown + turnDelay
    }
}