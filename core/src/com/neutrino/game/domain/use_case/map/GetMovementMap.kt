package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.entities.WoodenDoor
import com.neutrino.game.domain.model.entities.WoodenDoorArched
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
                val doors = level.getEntityWithAction(x, y)
                val isDoors = doors != null && (doors is WoodenDoor || doors is WoodenDoorArched)
                if (!level.doesAllowCharacter(x, y) && !isDoors)
                    movementMap[x][y] = '#'
            }
        }
        return movementMap
    }
}