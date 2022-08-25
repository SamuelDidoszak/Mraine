package com.neutrino.game

import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.level.LevelUseCases

class Initialize {
    val level: Level = LevelUseCases().getLevel(0, 0, 0)

    fun initialize() {
        level.characterArray.forEach {level.addActor(it)}
        level.provideTextures()
        // for whatever reason this is needed for level to access correct LevelChunkSize values
        println(level.sizeX.toString() + ", " + level.sizeY.toString())
    }
}