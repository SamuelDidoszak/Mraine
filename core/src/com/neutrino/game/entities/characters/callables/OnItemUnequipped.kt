package com.neutrino.game.entities.characters.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class OnItemUnequipped: Callable {
    /** @param data[[0]] entity: Item unequipped, or if callable is attached to item, data[0] is entity which unequipped it */
    abstract override fun call(entity: Entity, vararg data: Any?)
}