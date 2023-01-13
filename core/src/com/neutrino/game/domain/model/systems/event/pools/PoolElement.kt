package com.neutrino.game.domain.model.systems.event.pools

import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper

data class PoolElement (
    var eventWrapper: EventWrapper,
    var weight: Float,
    var multiplier: Float
)