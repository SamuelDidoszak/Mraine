package com.neutrino.game.entities.characters.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class OnItemEquipped: Callable {
    /** @param data[[0]] entity: Item equipped, or if callable is attached to item, data[0] is entity which equipped it */
    abstract override fun call(entity: Entity, vararg data: Any?)
}