package com.neutrino.game.map.generation.worldgen.fields

class FieldSampler(
    private val difficultyField: WorldField<Float>,
    private val richnessField: WorldField<Float>
) {

    fun sampleCell(
        worldX: Int,
        worldY: Int,
        z: Int
    ): Pair<Float, Float> {
        val difficulty = difficultyField.sample(worldX, worldY, z)
        val richness = richnessField.sample(worldX, worldY, z)
        return difficulty to richness
    }
}
