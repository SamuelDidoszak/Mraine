package com.neutrino.game.entities.util

interface Equality<T> {
    infix fun isEqual(other: T): Boolean
}