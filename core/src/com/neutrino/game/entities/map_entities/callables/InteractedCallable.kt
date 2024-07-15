package com.neutrino.game.entities.map_entities.callables

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class InteractedCallable: Callable {

    /** @param data[[0]] interactable attribute which called this */
    abstract override fun call(entity: Entity, vararg data: Any?)
}