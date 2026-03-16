package com.neutrino.game.map.generation.worldgen.biomes

import com.neutrino.game.map.generation.worldgen.GenerationContext
import com.neutrino.game.map.generation.worldgen.LootTable

data class Biome(
    val name: String,
    val itemList: LootTable,
    val enemyList: List<String>,
    val biomeFlags: BiomeFlags,
    private val biomeGenerator: (context: GenerationContext) -> Unit
) {
    fun generate(context: GenerationContext) = biomeGenerator.invoke(context)
}