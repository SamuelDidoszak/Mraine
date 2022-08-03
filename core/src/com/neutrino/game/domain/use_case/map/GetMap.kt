package com.neutrino.game.domain.use_case.map

import com.neutrino.game.SpriteHandler
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.map.Level

class GetMap (
) {
    operator fun invoke(level: Level): List<List<MutableList<Entity>>> {
        val spriteHandler = SpriteHandler(level.textureList[0])

        return List(level.sizeY){List(level.sizeX){ ArrayList() }}
    }
}