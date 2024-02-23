package com.neutrino.game.entities

interface Callable {
//    fun call(entity: Entity)

    /**
     * @return false if call chain should stop
     */
    fun call(entity: Entity, vararg data: Any?): Boolean
}