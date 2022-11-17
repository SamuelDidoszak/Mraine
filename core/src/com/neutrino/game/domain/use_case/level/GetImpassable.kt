package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.entities.utility.ChangesImpassable
import com.neutrino.game.domain.model.map.Level
import squidpony.squidmath.Coord

class GetImpassable(
    val level: Level
) {
    operator fun invoke(): List<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until level.map.yMax) {
            for (x in 0 until level.map.xMax) {
                for (entity in level.map.map[y][x]) {
                    if (entity is ChangesImpassable && !entity.allowCharacterOnTop) {
                        coordList.add(Coord.get(x, y))
                        break
                    }
                }
            }
        }
        return coordList
    }
}