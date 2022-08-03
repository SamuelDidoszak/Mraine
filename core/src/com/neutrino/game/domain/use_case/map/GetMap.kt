package com.neutrino.game.domain.use_case.map

import com.neutrino.game.SpriteHandler
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level
import java.util.*
import kotlin.collections.ArrayList

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