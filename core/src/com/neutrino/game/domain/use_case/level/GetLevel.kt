package com.neutrino.game.domain.use_case.level

import com.badlogic.gdx.Gdx
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.utility.serialization.Serializers
import kotlinx.serialization.decodeFromString


class GetLevel(

) {
    operator fun invoke(chunkCoords: LevelChunkCoords): Level {
        val levelId: Int = chunkCoords.toHash()

        val file = Gdx.files.local("saves/${levelId}")
        if (file.exists())
            return Serializers.format.decodeFromString(file.readString())

        val level: Level = GenerateLevel()(chunkCoords)
        return level
    }
}