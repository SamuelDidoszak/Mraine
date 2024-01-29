package com.neutrino.game.domain.use_case.map

import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.entities.map.attributes.MapParams

class GetMovementMap(
    private val level: Level
) {
    operator fun invoke(): Array<out CharArray> {
        try {
            level.map
        } catch (e: NullPointerException) {
            println("the map is not initialized")
            return arrayOf(CharArray(0))
        }
//        val movementMap: List<MutableList<Char>> = List(level.map.yMax) {MutableList(level.map.xMax) {'.'} }
        val movementMap: Array<out CharArray> = Array(level.sizeY) {CharArray(level.sizeX) {'.'} }
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (entity in level.map[y][x]) {
                    if(!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                        movementMap[x][y] = '#'
                        break
                    }
                }
            }
        }
        return movementMap
    }
}