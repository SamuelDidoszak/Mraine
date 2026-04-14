package com.neutrino.game.map.generation.worldgen

import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.generation.CharacterGenerator
import com.neutrino.game.map.generation.worldgen.bands.ElevationBand
import com.neutrino.game.map.generation.worldgen.bands.HumidityBand
import com.neutrino.game.map.generation.worldgen.bands.TemperatureBand
import com.neutrino.game.map.generation.worldgen.biomes.Biome
import com.neutrino.game.map.generation.worldgen.biomes.MacroBiomes
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.util.Constants

class ChunkGenerator(
    private val world: WorldContext
) {

    fun generate(chunkCoords: ChunkCoords): Chunk {
        return generate(chunkCoords.x, chunkCoords.y, chunkCoords.z)
    }

    fun generate(chunkX: Int, chunkY: Int, z: Int): Chunk {

        val centerX = chunkX * Constants.ChunkSize + Constants.ChunkSize / 2
        val centerY = chunkY * Constants.ChunkSize + Constants.ChunkSize / 2

        val temperature = world.temperature(centerX, centerY, z)
        val humidity = world.humidity(centerX, centerY, z)
        val elevation = world.elevation(centerX, centerY, z)

        val difficulty = world.difficulty(centerX, centerY, z)
        val richness = world.richness(centerX, centerY, z)

        val temperatureBand = TemperatureBand.get(temperature)
        val humidityBand = HumidityBand.get(humidity)
        val elevationBand = ElevationBand.get(elevation)

//        val macroBiome = MacroBiomes.get(
//            temperatureBand,
//            humidityBand,
//            elevationBand
//        )
        val macroBiome = MacroBiomes.get(
            TemperatureBand.Temperate,
            HumidityBand.Dry,
            ElevationBand.Medium
        )

        val biome = macroBiome.getVariation(
            world,
            centerX,
            centerY,
            z
        )

        val chunk = Chunk(ChunkCoords(chunkX, chunkY, z))
        val cells = world.biomeCellGrid.getChunkCells(chunkX, chunkY)

        val context = GenerationContext(
            world = world,
            chunk = chunk,
            difficulty = difficulty,
            richness = richness,
            biomeCells = cells
        )

        biome.generate(context)
//        addEnemies(biome, context)
        addItems(biome, context)

        chunk.afterMapGeneration()
        world.removeStoredChunkFields(context)

        return chunk
    }

    private fun addEnemies(biome: Biome, context: GenerationContext) {
        val characterGenerator = CharacterGenerator()
        characterGenerator.generate(context.chunk)
    }

    private fun addItems(biome: Biome, context: GenerationContext) { }
}