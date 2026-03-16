package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.generation.worldgen.biomes.BiomeCell

data class GenerationContext(
    val world: WorldContext,
    val chunk: Chunk,

    val difficulty: Double,
    val richness: Double,

    val biomeCells: List<List<BiomeCell>>
)