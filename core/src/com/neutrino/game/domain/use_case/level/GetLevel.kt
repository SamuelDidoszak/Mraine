package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.map.Level


class GetLevel(

) {
    operator fun invoke(chunkCoords: LevelChunkCoords): Level {
        val levelId: Int = chunkCoords.toHash()

        val level: Level = GenerateLevel()(chunkCoords)
        return level
    }
}