package com.neutrino.game.map.generation.algorithms

import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.Tileset
import com.neutrino.game.map.generation.algorithms.util.RoomFinderMethods
import com.neutrino.game.map.generation.util.EntityGenerationParams
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.generation.util.ModifyMap
import squidpony.squidgrid.mapping.RoomFinder

class RoomFinderAlgorithm(
    params: GenerationParams,
    sizeX: Int = params.map[0].size,
    sizeY: Int = params.map.size,
    modifyBaseMap: ModifyMap? = null
): GenerationAlgorithm(params, sizeX, sizeY, modifyBaseMap), RoomFinderMethods {

    private val roomFinder = RoomFinder(convertMap())

    override val roomEntities = ArrayList<EntityGenerationParams>()
    override val corridorEntities = ArrayList<EntityGenerationParams>()
    override val thisAlgorithm: RoomFinderAlgorithm = this

    override fun generate(tileset: Tileset): GenerationAlgorithm {
        generateInRooms()
        generateInCorridors()
        return this
    }

    fun generateInRooms(): GenerationAlgorithm {
        for (entityParams in roomEntities) {
            for (room in roomFinder.rooms.keys) {
                val decoded = room.decode().map { it.asList() }
                addEntities(decoded.indices, decoded[0].indices, decoded,
                    entityParams.id, entityParams.requirements, entityParams.amount, entityParams.asProbability, entityParams.replaceUnderneath)
            }
        }
        return this
    }

    fun generateInCorridors(): GenerationAlgorithm {
        for (entityParams in corridorEntities) {
            for (corridor in roomFinder.corridors.keys) {
                val decoded = corridor.decode().map { it.asList() }
                addEntities(decoded.indices, decoded[0].indices, decoded,
                    entityParams.id, entityParams.requirements, entityParams.amount, entityParams.asProbability, entityParams.replaceUnderneath)
            }
        }
        return this
    }

    override fun generateEntities(): GenerationAlgorithm {
        for (entityParams in entities) {
            for (room in roomFinder.rooms.keys) {
                val decoded = room.decode().map { it.asList() }
                addEntities(decoded.indices, decoded[0].indices, decoded,
                    entityParams.id, entityParams.requirements, entityParams.amount, entityParams.asProbability, entityParams.replaceUnderneath)
            }
        }
        return this
    }

    private fun convertMap(): Array<out CharArray> {
        val charMap = arrayOfNulls<CharArray>(sizeY)

        for (y in map.indices) {
            charMap[y] = (CharArray(sizeX))
            for (x in map[0].indices) {
                var isWall = false
                for (entity in map[y][x]) {
                    if (entity has Identity.Wall::class) {
                        charMap[y]!![x] = '#'
                        isWall = true
                        break
                    }
                }
                if (!isWall)
                    charMap[y]!![x] = '.'
            }
        }
        return charMap as Array<CharArray>
    }
}
