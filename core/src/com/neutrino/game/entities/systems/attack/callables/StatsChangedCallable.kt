package com.neutrino.game.entities.systems.attack.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class StatsChangedCallable: Callable {
    /**
     * @param data [[0]]: StatsEnum. Stat changed
     * @param data [[1]]: Float, Double or Int. Difference
     */
    abstract override fun call(entity: Entity, vararg data: Any?)
}