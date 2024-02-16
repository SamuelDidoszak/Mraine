package com.neutrino.game.domain.use_case.level

import com.neutrino.game.map.level.Chunk

data class LevelUseCases(
    val chunk: Chunk,
    val getImpassable: GetImpassable = GetImpassable(chunk)
)