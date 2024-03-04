package com.neutrino.game.entities

interface AttributeOperations<T: Attribute> {

    operator fun plus(other: T): T
    operator fun minus(other: T): T
    fun shouldRemove(): Boolean = false
    fun clone(): T
    infix fun isEqual(other: T): Boolean
}