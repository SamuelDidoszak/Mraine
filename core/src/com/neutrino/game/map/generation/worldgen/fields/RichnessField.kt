package com.neutrino.game.map.generation.worldgen.fields

import com.neutrino.game.map.generation.worldgen.util.FractalNoise
import squidpony.squidmath.OpenSimplex2F

class RichnessField(
    seed: Long
) : WorldField<Double> {

    val fractalNoise = FractalNoise(
        seed,
        0.0006,
        4
    )

    private val verticalNoise = OpenSimplex2F(seed + 9999L)

    override fun sample(x: Int, y: Int, z: Int): Double {

        val dx = x.toDouble()
        val dy = y.toDouble()

        val horizontal = fractalNoise.getNoise(dx, dy)
        val vertical = verticalModifier(z)

        val combined =
            0.75 * horizontal +
            0.25 * vertical

        val shaped = combined * combined

        return shaped.coerceIn(0.0, 1.0)
    }

    private fun verticalModifier(z: Int): Double {
        return when {
            z < 0 -> {
                // deeper underground = less richness
                val depth = (-z).toDouble()
                (1.0 / (1.0 + depth * 0.1)).coerceIn(0.0, 1.0)
            }
            z > 0 -> {
                // sky layers slightly richer
                0.6 + 0.1 * verticalNoise.getNoise(z * 0.1, 0.0)
            }
            else -> 0.5
        }
    }
}
