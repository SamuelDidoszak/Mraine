package com.neutrino.game.domain.use_case.level

import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.domain.model.map.Level
import kotlin.math.abs
import kotlin.math.max

class GenerateLevel(

) {
    operator fun invoke(chunkCoords: LevelChunkCoords): Level {
        val level: Level = Level(
            "test level",
            chunkCoords,
            "A level for testing map generation",
            sizeX = LevelChunkSize,
            sizeY = LevelChunkSize,
            0f,
            0f,
            getDifficultyFromDistance(chunkCoords.x, chunkCoords.y).toFloat(),
            null,
            null
        )

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