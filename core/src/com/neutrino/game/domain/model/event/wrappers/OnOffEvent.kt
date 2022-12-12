package com.neutrino.game.domain.model.event.wrappers

import com.neutrino.game.domain.model.event.Event

class OnOffEvent(
    override val event: Event
): EventWrapper(event)