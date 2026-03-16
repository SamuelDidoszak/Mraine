package com.neutrino.game.map.generation.worldgen.generators.util

class CircleMask(
    private val cx: Int,
    private val cy: Int,
    private val r: Int
) : GenerationMask {

    override fun contains(x: Int, y: Int): Boolean {
        val dx = x - cx
        val dy = y - cy
        return dx*dx + dy*dy <= r*r
    }
}