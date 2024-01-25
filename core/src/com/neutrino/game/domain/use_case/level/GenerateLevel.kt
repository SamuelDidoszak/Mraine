package com.neutrino.game.domain.use_case.level

import com.neutrino.game.util.Constants.LevelChunkSize
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.domain.use_case.map.GenerateMap
import kotlin.math.abs
import kotlin.math.max

class GenerateLevel(

) {
    operator fun invoke(chunkCoords: LevelChunkCoords): Level {
        val level: Level = Level(
            chunkCoords,
            "A level for testing map generation",
            sizeX = LevelChunkSize,
            sizeY = LevelChunkSize
        )

        level.map = GenerateMap(level)()
        level.movementMap = level.createMovementMap()
        val generateCharacters = GenerateCharacters(level)
        level.characterArray = generateCharacters.generate()
        level.characterMap = generateCharacters.characterMap
        level.provideTextures()

        return level
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