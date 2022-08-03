package com.neutrino.game.domain.use_case.map

import com.neutrino.game.SpriteHandler
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level
import java.util.*
import kotlin.collections.ArrayList

class GetMap (
) {
    operator fun invoke(level: Level): List<List<MutableList<Entity>>> {
        val map: List<List<MutableList<Entity>>> = List(level.sizeY) {
            List(level.sizeX) {
                arrayListOf(DungeonFloor())
            }
        }

        return map.ifEmpty { List(level.sizeY){List(level.sizeX){ ArrayList() }} }
    }
}