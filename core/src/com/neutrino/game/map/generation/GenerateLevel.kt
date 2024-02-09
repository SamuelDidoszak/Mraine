package com.neutrino.game.map.generation

import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.level.ChunkCoords
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.util.Constants.LevelChunkSize
import com.neutrino.generation.Tilesets
import kotlin.math.abs
import kotlin.math.max

class GenerateLevel(var levelDrawer: LevelDrawer) {

    private var tags: ArrayList<MapTag> = ArrayList()
    private var tagGenerators: ArrayList<() -> MapTag> = ArrayList()

    fun generate(chunkCoords: ChunkCoords): Level {
        val level: Level = Level(
            chunkCoords,
            "A level for testing map generation",
            sizeX = LevelChunkSize,
            sizeY = LevelChunkSize
        )

        level.map = List(level.sizeY) {
            List(level.sizeX) {
                java.util.ArrayList()
            }
        }

        tags.add(getDefaultMapTag())

        generateMap(level)
        level.movementMap = level.createMovementMap()
        val generateCharacters = CharacterGenerator(getParams(level), levelDrawer)
        level.characterArray = generateCharacters.generate()
        level.characterMap = generateCharacters.characterMap
        levelDrawer.initializeCharacterTextures(level.characterMap)

        return level
    }

    private fun generateMap(level: Level) {
        val params = getParams(level)
        for (generator in params.interpretedTags.mapGenerators) {
            generator.generate(params)
        }
        levelDrawer.currentLevel = level
        levelDrawer.map = level.map
        levelDrawer.initializeTextures(params.rng)
    }

    private fun getParams(level: Level): GenerationParams {
        return GenerationParams(
            MapTagInterpretation(
                tagGenerators.map { it.invoke() }.plus(tags)
            ),
            level.randomGenerator,
            level,
            level.map
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