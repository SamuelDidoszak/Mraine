package com.neutrino.game.map.generation.worldgen.fields

import com.neutrino.game.map.generation.worldgen.util.FractalNoise

class ElevationField(seed: Long) : WorldField<Double> {

    private val noise = FractalNoise(
         seed,
        1.0 / 800.0,
        5
    )

    override fun sample(x: Int, y: Int, z: Int): Double {
        return noise.getNoise(x.toDouble(), y.toDouble())
    }
}