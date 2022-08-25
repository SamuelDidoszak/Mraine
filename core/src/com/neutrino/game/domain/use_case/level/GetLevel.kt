package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.map.Level


class GetLevel(

) {
    operator fun invoke(xIndex: Int, yIndex: Int, zIndex: Int): Level {
        val levelId: Int = "$xIndex-$yIndex-$zIndex".hashCode()

        val level: Level = GenerateLevel()(xIndex, yIndex, zIndex)
        return level
    }
}