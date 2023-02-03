package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.map.Level

class GetMap (
    private val level: Level
) {
    operator fun invoke(): List<List<MutableList<Entity>>> {
        val map: List<List<MutableList<Entity>>> =
            // Search for a map with the level.id, else
            GenerateMap(level)()



        return map
    }
}