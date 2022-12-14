package com.neutrino.game.domain.model.event

interface Timed {
    /** Execution delay */
    val turnDelay: Double
    /** Length of delay before the next execution */
    val timeout: Double
    /** How many times the event will occur */
    var executions: Int

    fun getEventLength(): Double {
        return executions * timeout + turnDelay
    }
}