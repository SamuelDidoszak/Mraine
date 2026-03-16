package com.neutrino.game.map.generation.worldgen.bands

enum class HumidityBand {
    Dry,
    SemiDry,
    Moderate,
    Humid,
    Wet;

    companion object {
        fun get(humidity: Double): HumidityBand {
            return when (humidity) {
                in 0.0..0.2 -> Dry
                in 0.2..0.4 -> SemiDry
                in 0.4..0.6 -> Moderate
                in 0.6..0.8 -> Humid
                else -> Wet
            }
        }
    }
}