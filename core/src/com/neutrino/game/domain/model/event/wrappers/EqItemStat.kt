package com.neutrino.game.domain.model.event.wrappers

import com.neutrino.game.domain.model.event.types.EventModifyStat

class EqItemStat(
    override val event: EventModifyStat
): EventWrapper(event)