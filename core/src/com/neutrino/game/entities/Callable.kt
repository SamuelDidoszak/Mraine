package com.neutrino.game.entities

interface Callable {
    fun call(entity: Entity)

    fun call(entity: Entity, vararg data: Any?, callOthers: Boolean = false) {
        if (callOthers)
            call(entity)
    }
}