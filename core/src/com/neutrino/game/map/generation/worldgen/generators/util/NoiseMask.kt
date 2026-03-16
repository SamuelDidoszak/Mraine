package com.neutrino.game.map.generation.worldgen.generators.util

import squidpony.squidmath.OpenSimplex2F

class NoiseMask(
    private val noise: OpenSimplex2F,
    private val threshold: Double,
    private val scale: Double
) : GenerationMask {

    override fun contains(x: Int, y: Int): Boolean {
        return noise.getNoise(x * scale, y * scale) > threshold
    }
}