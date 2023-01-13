package com.neutrino.game.domain.model.systems.event.wrappers

import com.neutrino.game.domain.model.systems.event.Event

class OnOffEvent(
    override val event: Event
): EventWrapper(event)