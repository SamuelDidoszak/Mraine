package com.neutrino.game.map.generation.algorithms

import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.Tileset
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.generation.util.ModifyMap
import squidpony.squidgrid.mapping.RectangleRoomFinder

class RectangleRoomFinderAlgorithm(
    params: GenerationParams,
    sizeX: Int = params.map[0].size,
    sizeY: Int = params.map.size,
    modifyBaseMap: ModifyMap? = null
): GenerationAlgorithm(params, sizeX, sizeY, modifyBaseMap) {

    private val rooms = RectangleRoomFinder(convertMap()).findRectangles()

    override fun generate(tileset: Tileset): GenerationAlgorithm {
        return this
    }

    override fun generateEntities(): GenerationAlgorithm {
        for (entityParams in entities) {
            for (room in rooms) {
                addEntities(room.bottomLeft.y - room.height + 1 until room.bottomLeft.y + 1,
                    room.bottomLeft.x until room.bottomLeft.x + room.width, defaultBlockMap,
                    entityParams.id, entityParams.requirements, entityParams.amount, entityParams.asProbability, entityParams.replaceUnderneath)
            }
        }
        return this
    }

    private fun convertMap(): Array<out CharArray> {
        val charMap = arrayOfNulls<CharArray>(sizeX)

        for (x in map[0].indices) {
            charMap[x] = (CharArray(sizeY))
            for (y in map.indices) {
                var isWall = false
                for (entity in map[y][x]) {
                    if (entity has Identity.Wall::class) {
                        charMap[x]!![y] = '#'
                        isWall = true
                        break
                    }
                }
                if (!isWall)
                    charMap[x]!![y] = '.'
            }
        }
        return charMap as Array<CharArray>
    }
}
