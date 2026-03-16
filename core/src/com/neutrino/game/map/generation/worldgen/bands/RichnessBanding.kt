package com.neutrino.game.map.generation.worldgen.bands

class RichnessBanding(
    private val maxTier: Int
) {
    fun band(richness: Float): RichnessBand {
        val clamped = richness.coerceIn(0f, 1f)

        val scaled = clamped * maxTier
        val baseIndex = scaled.toInt().coerceIn(0, maxTier)

        val remainder = scaled - baseIndex
        val qualityOffset = (remainder * 2f) - 1f // [-1, 1]

        return RichnessBand(
            baseTier = ItemTier.values()[baseIndex],
            qualityOffset = qualityOffset,
            richness
        )
    }

    fun band(richness: Double): RichnessBand = band(richness.toFloat())
}
