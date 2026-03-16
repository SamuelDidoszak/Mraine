package com.neutrino.game.map.generation.worldgen.fields

interface WorldField<T> {
    fun sample(x: Int, y: Int, z: Int): T
}
