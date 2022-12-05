package com.neutrino.game.domain.model.event.wrappers

import com.neutrino.game.domain.model.event.Event
import com.neutrino.game.domain.model.event.Timed

data class TimedEvent(
    /** Execution delay */
    override val turnDelay: Double,
    /** After how long is the next turn */
    override val timeout: Double,
    /** How many times the event will occur */
    override var executions: Int,
    override val event: Event
): EventWrapper(event), Timed