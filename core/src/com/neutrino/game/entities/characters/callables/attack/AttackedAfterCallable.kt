package com.neutrino.game.entities.characters.callables.attack

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class AttackedAfterCallable: Callable {

    /**
     * @param data [[0]] Entity: Attacked Entity.
     * @param data [[1]] Float?: Damage dealt. null if evaded the attack
     */
    abstract override fun call(entity: Entity, vararg data: Any?): Boolean
}