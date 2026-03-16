package com.neutrino.game.map.generation.worldgen.fields

import com.neutrino.game.map.generation.worldgen.util.FractalNoise

class HumidityField(seed: Long) : WorldField<Double> {

    private val noise = FractalNoise(
        seed,
        1.0 / 1200.0,
        4
    )

    override fun sample(x: Int, y: Int, z: Int): Double {
        return noise.getNoise(x.toDouble(), y.toDouble())
    }
}