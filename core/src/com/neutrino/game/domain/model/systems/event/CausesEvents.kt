package com.neutrino.game.domain.model.systems.event

import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper

interface CausesEvents {
    val eventWrappers: List<EventWrapper>
}