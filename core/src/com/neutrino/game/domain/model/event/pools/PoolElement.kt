package com.neutrino.game.domain.model.event.pools

import com.neutrino.game.domain.model.event.wrappers.EventWrapper

data class PoolElement (
    var eventWrapper: EventWrapper,
    var weight: Float,
    var multiplier: Float
)