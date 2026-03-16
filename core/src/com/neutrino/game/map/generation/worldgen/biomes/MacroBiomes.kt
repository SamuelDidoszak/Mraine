package com.neutrino.game.map.generation.worldgen.biomes

import com.neutrino.game.map.generation.worldgen.bands.ElevationBand
import com.neutrino.game.map.generation.worldgen.bands.HumidityBand
import com.neutrino.game.map.generation.worldgen.bands.TemperatureBand

object MacroBiomes {
    private val macroBiomeMap: HashMap<Int, MacroBiome> = HashMap()

    fun add(macroBiome: MacroBiome): MacroBiome {
        macroBiomeMap[macroBiome.id] = macroBiome
        return macroBiome
    }

    /**
     * Tries to return a macro biome with provided elevation. If none found, returns a generic macro biome based on temperature and humidity
     */
    fun get(temperature: TemperatureBand,
            humidity: HumidityBand,
            elevation: ElevationBand
    ): MacroBiome {
        val id = humidity.ordinal + temperature.ordinal * 10 + elevation.ordinal * 100
        return macroBiomeMap[id] ?: macroBiomeMap[id % 100]!!
    }

    fun MacroBiome.addCopy(
        temperature: TemperatureBand,
        humidity: HumidityBand,
        elevation: ElevationBand? = null): MacroBiome {
        val newMacroBiome = this.copy(
            temperature = temperature,
            humidity = humidity,
            elevation = elevation
        )
        add(newMacroBiome)
        return newMacroBiome
    }
}