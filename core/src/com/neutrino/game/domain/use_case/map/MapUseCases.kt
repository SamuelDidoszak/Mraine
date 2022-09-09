package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.map.Level

data class MapUseCases(
    val level: Level,
    val getMap: GetMap = GetMap(level),
    val getMovementMap: GetMovementMap = GetMovementMap(level),
    val getItemsFromLevelTags: GetItemsFromLevelTags = GetItemsFromLevelTags(level)
)