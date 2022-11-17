package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.map.Level

data class LevelUseCases(
    val level: Level,
    val getImpassable: GetImpassable = GetImpassable(level)
)