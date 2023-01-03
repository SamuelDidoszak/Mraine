package com.neutrino.game

import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.use_case.level.GetLevel

class Initialize {
    val level: Level = GetLevel()(0, 0, 0)

    fun initialize() {
        level.characterArray.forEach {level.addActor(it)}
    }
}