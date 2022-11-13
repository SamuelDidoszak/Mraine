package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.entities.utility.ChangesImpassable
import com.neutrino.game.domain.model.map.Level

class GetMovementMap(
    private val level: Level
) {
    operator fun invoke(): Array<out CharArray> {
        try {
            level.map.map
        } catch (e: NullPointerException) {
            println("the map is not initialized")
            return arrayOf(CharArray(0))
        }
//        val movementMap: List<MutableList<Char>> = List(level.map.yMax) {MutableList(level.map.xMax) {'.'} }
        val movementMap: Array<out CharArray> = Array(level.map.yMax) {CharArray(level.map.xMax) {'.'} }
        for (y in 0 until level.map.yMax) {
            for (x in 0 until level.map.xMax) {
                for (entity in level.map.map[y][x]) {
                    if (!entity.allowCharacterOnTop && entity !is ChangesImpassable) {
                        movementMap[x][y] = '#'
                        break
                    }
                }
            }
        }
        return movementMap
    }
}