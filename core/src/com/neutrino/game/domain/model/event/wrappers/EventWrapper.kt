package com.neutrino.game.domain.model.event.wrappers

import com.neutrino.game.domain.model.event.Event

open class EventWrapper (
    open val event: Event<*>
)