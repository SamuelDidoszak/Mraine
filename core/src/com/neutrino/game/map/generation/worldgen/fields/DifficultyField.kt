package com.neutrino.game.map.generation.worldgen.fields

import com.neutrino.game.map.generation.worldgen.util.FractalNoise
import kotlin.math.abs
import kotlin.math.sqrt

class DifficultyField(
    seed: Long,
    private val worldRadius: Double
) : WorldField<Double> {

    val fractalNoise = FractalNoise(
        seed,
        0.0008,
        4
    )

    override fun sample(x: Int, y: Int, z: Int): Double {
        val dx = x.toDouble()
        val dy = y.toDouble()

        val radial = radialDifficulty(dx, dy)
        val variation = fractalNoise.getNoise(dx, dy)

        val verticalModifier = verticalDifficulty(z)

        val combined =
            0.7 * radial +
            0.25 * variation +
            0.05 * verticalModifier

        return combined.coerceIn(0.0, 1.0)
    }

    private fun radialDifficulty(x: Double, y: Double): Double {
        val dist = sqrt(x * x + y * y)
        return (dist / worldRadius).coerceIn(0.0, 1.0)
    }

    private fun verticalDifficulty(z: Int): Double {
        return abs(z) * 0.05 * if (z < 0) 1 else 2 / 3
    }
}
