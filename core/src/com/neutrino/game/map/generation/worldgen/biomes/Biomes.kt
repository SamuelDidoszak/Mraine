package com.neutrino.game.map.generation.worldgen.biomes

object Biomes {
    private val biomeMap: HashMap<String, Biome> = HashMap()

    fun add(biome: Biome): Biome {
        biomeMap[biome.name] = biome
        return biome
    }

    fun get(biome: String): Biome {
        return biomeMap[biome] ?: throw Exception("There is no $biome biome!")
    }
}