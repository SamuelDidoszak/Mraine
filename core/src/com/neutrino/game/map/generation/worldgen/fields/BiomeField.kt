package com.neutrino.game.map.generation.worldgen.fields

import com.neutrino.game.map.generation.worldgen.bands.ElevationBand
import com.neutrino.game.map.generation.worldgen.bands.HumidityBand
import com.neutrino.game.map.generation.worldgen.bands.RichnessBanding
import com.neutrino.game.map.generation.worldgen.bands.TemperatureBand
import com.neutrino.game.map.generation.worldgen.biomes.Biome
import com.neutrino.game.map.generation.worldgen.biomes.Biomes
import com.neutrino.game.map.generation.worldgen.biomes.MacroBiomes
import com.neutrino.game.util.Constants

class BiomeField(
    private val temperature: TemperatureField,
    private val humidity: HumidityField,
    private val elevation: ElevationField,
    private val difficulty: DifficultyField,
    private val richnessField: RichnessField
) : WorldField<Biome> {

    override fun sample(x: Int, y: Int, z: Int): Biome {
        val temperature = temperature.sample(x, y, z)
        val humidity = humidity.sample(x, y, z)
        val elevation = elevation.sample(x, y, z)
        val difficulty = difficulty.sample(x, y, z)
        val richness = richnessField.sample(x, y, z)

        val temperatureBand = TemperatureBand.get(temperature)
        val humidityBand = HumidityBand.get(humidity)
        val elevationBand = ElevationBand.get(elevation)
        val richnessBand = RichnessBanding(Constants.maxItemTier).band(richness)

        val macroBiome = MacroBiomes.get(temperatureBand, humidityBand, elevationBand)
        return Biomes.get("Green plains")
    }
}
