package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.domain.model.systems.event.Timed
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class TimedEvent(
    /** Execution delay */
    override val turnDelay: Double,
    /** After how long is the next turn */
    override val cooldown: Double,
    /** How many times the event will occur */
    override var executions: Int,
    @Polymorphic override val event: Event
): EventWrapper(), Timed {
    /**
     * Creates an event that lasts infinitely
     */
    constructor(onOffEvent: OnOffEvent) :
            this(0.0, Double.MAX_VALUE, 1, onOffEvent.event)

    /**
     * Creates an event that is executed once and lasts for provided turns
     */
    constructor(turns: Double, event: Event) :
            this(0.0, turns, 1, event)
}