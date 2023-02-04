package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event
import kotlinx.serialization.Serializable

@Serializable
abstract class EventWrapper {
    abstract val event: Event
}