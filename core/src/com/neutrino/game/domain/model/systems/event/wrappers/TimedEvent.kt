package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.domain.model.systems.event.Timed

data class TimedEvent(
    /** Execution delay */
    override val turnDelay: Double,
    /** After how long is the next turn */
    override val timeout: Double,
    /** How many times the event will occur */
    override var executions: Int,
    override val event: Event
): EventWrapper(event), Timed {
    constructor(onOffEvent: OnOffEvent) :
            this(0.0, Double.MAX_VALUE, 1, onOffEvent.event)
}