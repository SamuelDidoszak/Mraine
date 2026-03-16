package com.neutrino.game.map.generation.worldgen.bands

enum class TemperatureBand {
    VeryCold,
    Cold,
    Temperate,
    Hot,
    VeryHot;

    companion object {
        fun get(temperature: Double): TemperatureBand {
            return when (temperature) {
                in 0.0..0.2 -> VeryCold
                in 0.2..0.4 -> Cold
                in 0.4..0.6 -> Temperate
                in 0.6..0.8 -> Hot
                else -> VeryHot
            }
        }
    }
}