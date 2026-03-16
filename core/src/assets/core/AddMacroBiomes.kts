import com.neutrino.game.map.generation.worldgen.bands.HumidityBand
import com.neutrino.game.map.generation.worldgen.bands.TemperatureBand
import com.neutrino.game.map.generation.worldgen.biomes.Biomes
import com.neutrino.game.map.generation.worldgen.biomes.MacroBiome
import com.neutrino.game.map.generation.worldgen.biomes.MacroBiomes
import com.neutrino.game.map.generation.worldgen.biomes.MacroBiomes.addCopy

MacroBiomes.add(
    MacroBiome(
        name = "Desert",
        temperature = TemperatureBand.VeryHot,
        humidity = HumidityBand.Dry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Desert")
})).addCopy(
    temperature = TemperatureBand.Hot,
    humidity = HumidityBand.Dry
)

MacroBiomes.add(
    MacroBiome(
        name = "Savanna",
        temperature = TemperatureBand.VeryHot,
        humidity = HumidityBand.SemiDry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Savanna")
})).addCopy(
    temperature = TemperatureBand.Hot,
    humidity = HumidityBand.SemiDry
)

MacroBiomes.add(
    MacroBiome(
        name = "Tropical forest",
        temperature = TemperatureBand.VeryHot,
        humidity = HumidityBand.Moderate,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Tropical forest")
})).addCopy(
    temperature = TemperatureBand.VeryHot,
    humidity = HumidityBand.Humid
)

MacroBiomes.add(
    MacroBiome(
        name = "Tropical rainforest",
        temperature = TemperatureBand.VeryHot,
        humidity = HumidityBand.Wet,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Tropical rainforest")
})).addCopy(
    temperature = TemperatureBand.Hot,
    humidity = HumidityBand.Wet
)

MacroBiomes.add(
    MacroBiome(
        name = "Tropical beach",
        temperature = TemperatureBand.Hot,
        humidity = HumidityBand.Moderate,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Tropical beach")
})).addCopy(
    temperature = TemperatureBand.Hot,
    humidity = HumidityBand.Humid
)

MacroBiomes.add(
    MacroBiome(
        name = "Green plains",
        temperature = TemperatureBand.Temperate,
        humidity = HumidityBand.Dry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Green plains")
})).addCopy(
    temperature = TemperatureBand.Temperate,
    humidity = HumidityBand.SemiDry
).addCopy(
    temperature = TemperatureBand.Cold,
    humidity = HumidityBand.Moderate
)

MacroBiomes.add(
    MacroBiome(
        name = "Forest",
        temperature = TemperatureBand.Temperate,
        humidity = HumidityBand.Moderate,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Forest")
})).addCopy(
    temperature = TemperatureBand.Temperate,
    humidity = HumidityBand.Humid
).addCopy(
    temperature = TemperatureBand.Cold,
    humidity = HumidityBand.Humid
)

MacroBiomes.add(
    MacroBiome(
        name = "Swamp",
        temperature = TemperatureBand.Temperate,
        humidity = HumidityBand.Wet,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Swamp")
        }
    )
)

MacroBiomes.add(
    MacroBiome(
        name = "Rocky cold desert",
        temperature = TemperatureBand.Cold,
        humidity = HumidityBand.Dry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Rocky cold desert")
        }
    )
)

MacroBiomes.add(
    MacroBiome(
        name = "Tundra",
        temperature = TemperatureBand.Cold,
        humidity = HumidityBand.SemiDry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Tundra")
})).addCopy(
    temperature = TemperatureBand.VeryCold,
    humidity = HumidityBand.SemiDry
)

MacroBiomes.add(
    MacroBiome(
        name = "Taiga",
        temperature = TemperatureBand.Cold,
        humidity = HumidityBand.Wet,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Taiga")
        }
    )
)

MacroBiomes.add(
    MacroBiome(
        name = "Arctic",
        temperature = TemperatureBand.VeryCold,
        humidity = HumidityBand.Dry,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Arctic")
})).addCopy(
    temperature = TemperatureBand.VeryCold,
    humidity = HumidityBand.Wet
)

MacroBiomes.add(
    MacroBiome(
        name = "Winter forest",
        temperature = TemperatureBand.VeryCold,
        humidity = HumidityBand.Moderate,
        biomeVariations = { worldContext, x, y, z ->
            Biomes.get("Winter forest")
})).addCopy(
    temperature = TemperatureBand.VeryCold,
    humidity = HumidityBand.Humid
)