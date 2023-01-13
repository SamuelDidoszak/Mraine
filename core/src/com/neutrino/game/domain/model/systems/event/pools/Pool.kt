package com.neutrino.game.domain.model.systems.event.pools

open class Pool {
    /**
     * Pool of events, where pair.first is an event and second is its power
     */
    open var eventPool: MutableList<PoolElement> = mutableListOf()

    class AllStatsPool: Pool() {
        override var eventPool: MutableList<PoolElement> = mutableListOf(
//            PoolElement(OnOffEvent(EventModifyStat(StatsEnum.HPMAX)))
        )
    }
}