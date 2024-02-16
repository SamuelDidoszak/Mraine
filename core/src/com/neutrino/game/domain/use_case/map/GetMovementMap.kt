package com.neutrino.game.domain.use_case.map

import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.map.level.Chunk
import com.neutrino.game.entities.map.attributes.MapParams

class GetMovementMap(
    private val chunk: Chunk
) {
    operator fun invoke(): Array<out CharArray> {
        try {
            chunk.map
        } catch (e: NullPointerException) {
            println("the map is not initialized")
            return arrayOf(CharArray(0))
        }
//        val movementMap: List<MutableList<Char>> = List(level.map.yMax) {MutableList(level.map.xMax) {'.'} }
        val movementMap: Array<out CharArray> = Array(chunk.sizeY) {CharArray(chunk.sizeX) {'.'} }
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
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