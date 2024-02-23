package com.neutrino.game.entities.characters.callables.attack

import com.neutrino.game.entities.Callable
import com.neutrino.game.entities.Entity

abstract class GotAttackedAfterCallable: Callable {
    /**
     * @param data [[0]]: Attacker entity
     * @param data [[1]]: Damage dealt. null if evaded the attack
     */
    abstract override fun call(entity: Entity, vararg data: Any?): Boolean
}