package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event

abstract class EventWrapper {
    abstract val event: Event
}