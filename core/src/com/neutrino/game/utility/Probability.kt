package com.neutrino.game.utility

data class Probability<T: Any>(val value: T, val probability: Float) {
    override fun toString(): String {
        return "$value, ${probability}%"
    }
}