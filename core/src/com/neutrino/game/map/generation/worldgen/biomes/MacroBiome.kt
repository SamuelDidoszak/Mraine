package com.neutrino.game.map.generation.worldgen.biomes

import com.neutrino.game.map.generation.worldgen.WorldContext
import com.neutrino.game.map.generation.worldgen.bands.ElevationBand
import com.neutrino.game.map.generation.worldgen.bands.HumidityBand
import com.neutrino.game.map.generation.worldgen.bands.TemperatureBand

data class MacroBiome(
    val name: String,
    val temperature: TemperatureBand,
    val humidity: HumidityBand,
    val elevation: ElevationBand? = null,
    private val biomeVariations: (WorldContext, Int, Int, Int) -> Biome
) {
    fun getVariation(
        worldContext: WorldContext,
        x: Int,
        y: Int,
        z: Int
    ) = biomeVariations.invoke(worldContext, x, y, z)

    val id: Int = humidity.ordinal + temperature.ordinal * 10 + (elevation?.ordinal ?: 0) * 100
}