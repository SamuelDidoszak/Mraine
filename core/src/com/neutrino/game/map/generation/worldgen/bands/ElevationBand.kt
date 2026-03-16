package com.neutrino.game.map.generation.worldgen.bands

enum class ElevationBand {
    Water,
    Beach,
    Low,
    Medium,
    High,
    VeryHigh;

    companion object {
        fun get(elevation: Double): ElevationBand {
            return when (elevation) {
                in 0.0..0.3 -> Water
                in 0.3..0.32 -> Beach
                in 0.32..0.45 -> Low
                in 0.45..0.7 -> Medium
                in 0.7..0.9 -> High
                else -> VeryHigh
            }
        }
    }
}