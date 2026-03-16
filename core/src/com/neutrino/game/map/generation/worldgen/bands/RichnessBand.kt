package com.neutrino.game.map.generation.worldgen.bands

import kotlin.random.Random

data class RichnessBand(
    val baseTier: ItemTier,
    val qualityOffset: Float,
    val totalQuality: Float
) {

    fun selectTier(
        rng: Random
    ): ItemTier {
        val base = baseTier.index
        val roll = rng.nextFloat()

        return when {
            qualityOffset > 0f && roll < qualityOffset * 0.5f ->
                ItemTier.values().getOrNull(base + 1) ?: baseTier

            qualityOffset < 0f && roll < -qualityOffset * 0.5f ->
                ItemTier.values().getOrNull(base - 1) ?: baseTier

            else -> baseTier
        }
    }
}