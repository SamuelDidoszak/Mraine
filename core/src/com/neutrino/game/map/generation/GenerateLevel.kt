package com.neutrino.game.map.generation

import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkCoords
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.utility.Probability
import kotlin.math.abs
import kotlin.math.max

class GenerateLevel() {

    private var tagList: ArrayList<MapTag> = ArrayList<MapTag>().also { it.add(getDefaultMapTag()) }
    private var tagGenerators: ArrayList<() -> MapTag> = ArrayList()

    fun generate(chunkCoords: ChunkCoords): Chunk {
        val chunk: Chunk = Chunk(chunkCoords)
        chunk.tagList = tagList
        val params = getParams(chunk)

        generateMap(chunk, params)
        ItemGenerator(chunk, params).generate()
        chunk.afterMapGeneration()
        val generateCharacters = CharacterGenerator(getParams(chunk))
        chunk.characterArray = generateCharacters.generate()
        chunk.characterMap = generateCharacters.characterMap

        return chunk
    }

    private fun generateMap(chunk: Chunk, params: GenerationParams) {
        for (generator in params.interpretedTags.mapGenerators) {
            generator.generate(params)
        }
    }

    private fun getParams(chunk: Chunk): GenerationParams {
        return GenerationParams(
            MapTagInterpretation(
                tagGenerators.map { it.invoke() }.plus(chunk.tagList)
            ),
            chunk.randomGenerator,
            chunk,
            chunk.map
        )
    }

    private fun getDefaultMapTag(): MapTag {
        return MapTag(
            listOf(Tilesets.get("Dungeon")),
            listOf(Generators.get("Dungeon")),
            listOf("Mouse", "Slime"),
            listOf(40f to "Gold", 5f to "Dagger", 15f to "Small healing potion", 60f to "Meat").toProbabilityList(),
            TagParams(100f),
            true
        )
    }

    private fun List<Pair<Float, String>>.toProbabilityList() = map { Probability(it) }

    private fun getDifficultyFromDistance(xIndex: Int, yIndex: Int): Int {
        val distance = max(abs(xIndex), abs(yIndex))

        /** Difficulty list where first is difficulty and second is distance */
        val difficultyList: List<Pair<Int, Int>> = listOf(
            Pair(1, 0),
            Pair(2, 2),
            Pair(3, 5),
            Pair(4, 10),
            Pair(5, 20)
        )
        for (i in 1 until difficultyList.size) {
            if (difficultyList[i].second < distance)
                return difficultyList[i - 1].first
        }
        return difficultyList.last().first
    }
}