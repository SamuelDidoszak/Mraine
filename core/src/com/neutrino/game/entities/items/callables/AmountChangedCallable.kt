package com.neutrino.game.entities.items.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class AmountChangedCallable: Callable {
    /**
     * @param data [[0]]: Current amount
     */
    abstract override fun call(entity: Entity, vararg data: Any?): Boolean
}