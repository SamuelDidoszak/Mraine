package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute

sealed class Identity: Attribute() {

    class Any: Identity()
    class Floor: Identity()
    class Wall: Identity()
    class StairsUp: Identity()
    class StairsDown: Identity()
    class Door: Identity()
    class Torch: Identity()
    class Container: Identity()

    override fun equals(other: kotlin.Any?): Boolean {
        return this::class == other!!::class
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return this::class.simpleName!!
    }
}