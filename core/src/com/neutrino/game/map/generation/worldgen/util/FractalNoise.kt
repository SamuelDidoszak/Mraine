package com.neutrino.game.map.generation.worldgen.util

import squidpony.squidmath.OpenSimplex2F

class FractalNoise(
    private val seed: Long,
    private val baseScale: Double,
    private val octaves: Int,
    private val persistence: Double = 0.5
) {

    val simplexNoise = OpenSimplex2F(seed)

    fun getNoise(x: Double, y: Double): Double {
        var frequency = baseScale
        var amplitude = 1.0
        var max = 0.0
        var sum = 0.0

        repeat(octaves) {
            sum += simplexNoise.getNoise(x * frequency, y * frequency) * amplitude
            max += amplitude
            amplitude *= persistence
            frequency *= 2.0
        }

        val normalized = sum / max
        return (normalized + 1.0) * 0.5
    }
}