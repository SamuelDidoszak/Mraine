package com.neutrino.game.domain.model.event

import com.neutrino.game.domain.model.event.wrappers.EventWrapper

interface CausesEvents {
    val eventWrappers: List<EventWrapper>
}