package com.neutrino.game.entities.util

import com.neutrino.game.entities.Attribute

interface AttributeOperations<T: Attribute>: Equality<T>, Cloneable<T> {
    fun plusEquals(other: T)
    fun minusEquals(other: T)
    fun shouldRemove(): Boolean = false
}