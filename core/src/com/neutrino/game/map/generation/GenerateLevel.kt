package com.neutrino.game.map.generation

import com.neutrino.game.domain.use_case.level.ChunkCoords
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.level.Chunk
import com.neutrino.generation.Tilesets
import kotlin.math.abs
import kotlin.math.max

class GenerateLevel() {

    private var tags: ArrayList<MapTag> = ArrayList()
    private var tagGenerators: ArrayList<() -> MapTag> = ArrayList()

    fun generate(chunkCoords: ChunkCoords): Chunk {
        val chunk: Chunk = Chunk(chunkCoords)

        chunk.map = List(chunk.sizeY) {
            List(chunk.sizeX) {
                java.util.ArrayList()
            }
        }

        tags.add(getDefaultMapTag())

        generateMap(chunk)
        chunk.movementMap = chunk.createMovementMap()
        val generateCharacters = CharacterGenerator(getParams(chunk))
        chunk.characterArray = generateCharacters.generate()
        chunk.characterMap = generateCharacters.characterMap

        return chunk
    }

    private fun generateMap(chunk: Chunk) {
        val params = getParams(chunk)
        for (generator in params.interpretedTags.mapGenerators) {
            generator.generate(params)
        }
    }

    private fun getParams(chunk: Chunk): GenerationParams {
        return GenerationParams(
            MapTagInterpretation(
                tagGenerators.map { it.invoke() }.plus(tags)
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
            listOf(),
            listOf(),
            TagParams(10f),
            true
        )
    }

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