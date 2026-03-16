package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.map.generation.worldgen.biomes.BiomeCellGrid
import com.neutrino.game.map.generation.worldgen.fields.*
import com.neutrino.game.util.Constants
import com.neutrino.game.util.SeedUtil

class WorldContext {

    val worldSeed = Constants.Seed

    private val temperatureSeed = SeedUtil.branch(worldSeed, "temperature")
    private val humiditySeed = SeedUtil.branch(worldSeed, "humidity")
    private val elevationSeed = SeedUtil.branch(worldSeed, "elevation")
    private val difficultySeed = SeedUtil.branch(worldSeed, "difficulty")
    private val richnessSeed = SeedUtil.branch(worldSeed, "richness")

    private val cellSeed = SeedUtil.branch(worldSeed, "biomeCells")

    private val temperatureField = TemperatureField(temperatureSeed)
    private val humidityField = HumidityField(humiditySeed)
    private val elevationField = ElevationField(elevationSeed)
    private val difficultyField = DifficultyField(difficultySeed, worldRadius = 40000.0)
    private val richnessField = RichnessField(richnessSeed)

    private val savedTemperature = HashMap<Int, HashMap<Int, Double>>()
    private val savedHumidity = HashMap<Int, HashMap<Int, Double>>()
    private val savedElevation = HashMap<Int, HashMap<Int, Double>>()
    private val savedDifficulty = HashMap<Int, HashMap<Int, Double>>()
    private val savedRichness = HashMap<Int, HashMap<Int, Double>>()

    val biomeCellGrid = BiomeCellGrid(worldSeed = cellSeed)

    fun temperature(x: Int, y: Int, z: Int): Double {
        return savedTemperature.getOrPut(x) { HashMap() }.getOrPut(y) { temperatureField.sample(x, y, z) }
    }

    fun humidity(x: Int, y: Int, z: Int): Double {
        return savedHumidity.getOrPut(x) { HashMap() }.getOrPut(y) { humidityField.sample(x, y, z) }
    }

    fun elevation(x: Int, y: Int, z: Int): Double {
        return savedElevation.getOrPut(x) { HashMap() }.getOrPut(y) { elevationField.sample(x, y, z) }
    }

    fun difficulty(x: Int, y: Int, z: Int): Double {
        return savedDifficulty.getOrPut(x) { HashMap() }.getOrPut(y) { difficultyField.sample(x, y, z) }
    }

    fun richness(x: Int, y: Int, z: Int): Double {
        return savedRichness.getOrPut(x) { HashMap() }.getOrPut(y) { richnessField.sample(x, y, z) }
    }

    /**
     * Sampled field data gets stored in temporary lists. Call this function after chunk generation to free up memory
     */
    fun removeStoredChunkFields(generationContext: GenerationContext) {
        generationContext.biomeCells.forEach { it.forEach {
            savedDifficulty[it.x]?.remove(it.y)
            savedRichness[it.x]?.remove(it.y)
        } }
        val chunkCoords = generationContext.chunk.chunkCoords
        for (x in chunkCoords.x * Constants.ChunkSize until (chunkCoords.x + 1) * Constants.ChunkSize) {
            for (y in chunkCoords.y * Constants.ChunkSize until (chunkCoords.y + 1) * Constants.ChunkSize) {
                savedTemperature[x]?.remove(y)
            }
        }
    }
}