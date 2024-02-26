package com.neutrino.game.utility

data class Probability<T: Any>(val value: T, val probability: Float) {
    constructor(pair: Pair<Float, T>): this(pair.second, pair.first)
    override fun toString(): String {
        return "$value, ${probability}%"
    }
}