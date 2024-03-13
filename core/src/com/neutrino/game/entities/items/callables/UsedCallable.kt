package com.neutrino.game.entities.items.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class UsedCallable: Callable {
    /** @param data [[0]]: Entity upon which use() was called */
    abstract override fun call(entity: Entity, vararg data: Any?): Boolean
}