package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
class OnOffEvent(
    @Polymorphic override val event: Event
): EventWrapper()