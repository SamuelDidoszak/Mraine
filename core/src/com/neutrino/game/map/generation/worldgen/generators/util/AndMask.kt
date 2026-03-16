package com.neutrino.game.map.generation.worldgen.generators.util

class AndMask(
    private val a: GenerationMask,
    private val b: GenerationMask
) : GenerationMask {

    override fun contains(x: Int, y: Int): Boolean {
        return a.contains(x,y) && b.contains(x,y)
    }
}