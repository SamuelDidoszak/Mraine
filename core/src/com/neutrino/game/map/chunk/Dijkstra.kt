package com.neutrino.game.map.chunk

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.util.Constants.ChunkSize
import com.neutrino.game.util.compareDelta
import com.neutrino.game.util.equalsDelta
import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.Measurement
import squidpony.squidmath.Coord
import kotlin.math.abs

class Dijkstra {

    private val dijkstraMaps: List<DijkstraElement> = List(4) { DijkstraElement() }
    private val chunkDijkstraMaps: MutableMap<ChunkCoords, Array<out CharArray>> = mutableMapOf()
    val impassables = Impassables()

    private fun get(chunkCoords: ChunkCoords): DijkstraElement {
        var unusedDijkstra = 0
        for (i in dijkstraMaps.indices) {
            if (dijkstraMaps[i].isIn(chunkCoords)) {
                dijkstraMaps[i].turnUsed = Turn.turn
                return dijkstraMaps[i]
            }

            if (dijkstraMaps[i].turnUsed.compareDelta(dijkstraMaps[unusedDijkstra].turnUsed) == -1)
                unusedDijkstra = i
        }

        dijkstraMaps[unusedDijkstra].initializeWithMiddleChunk(chunkCoords, chunkDijkstraMaps)
        dijkstraMaps[unusedDijkstra].turnUsed = Turn.turn
        dijkstraMaps[unusedDijkstra].print()
        return dijkstraMaps[unusedDijkstra]
    }

    fun initializeChunk(chunk: Chunk) {
        chunkDijkstraMaps[chunk.chunkCoords] = createDijkstraMap(chunk)
        updateNearbyChunks(chunk.chunkCoords)
        impassables.register(chunk)
    }

    fun removeChunk(chunk: Chunk) {
        chunkDijkstraMaps.remove(chunk.chunkCoords)
        updateNearbyChunks(chunk.chunkCoords)
        impassables.unregister(chunk)
    }

    fun getPath(entity: Entity, position: Position): List<Position> {
        val entityPosition = entity.get(Position::class)!!.getCorrectPosition()
        val position = position.getCorrectPosition()

        val dijkstra = get(position.chunkCoords)
        return dijkstra.getPath(entityPosition, position, impassables.getByMiddleChunk(position.chunkCoords))
    }

    private fun updateNearbyChunks(chunkCoords: ChunkCoords) {
        val dijkstraMap = chunkDijkstraMaps[chunkCoords]
        dijkstraMaps.forEach {
            val middleChunk = it.middleChunk ?: return@forEach
            if (middleChunk.x in chunkCoords.x - 1 .. chunkCoords.x + 1
                && middleChunk.y in chunkCoords.y - 1 .. chunkCoords.y + 1
                && middleChunk.z == chunkCoords.z) {
                val cOffsetX = ChunkSize + ChunkSize * (chunkCoords.x - it.middleChunk!!.x)
                val cOffsetY = ChunkSize - ChunkSize * (chunkCoords.y - it.middleChunk!!.y)
                for (x in 0 until ChunkSize) {
                    for (y in 0 until ChunkSize) {
                        it.map[cOffsetX + x][cOffsetY + y] = dijkstraMap?.get(x)?.get(y) ?: '#'
                    }
                }
                it.dijkstraMap.initialize(it.map)
            }
        }
    }


    private fun createDijkstraMap(chunk: Chunk): Array<out CharArray> {
        val movementMap: Array<out CharArray> = Array(chunk.sizeX) {CharArray(chunk.sizeY) {'.'} }
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
                    if (!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                        movementMap[x][y] = '#'
                        break
                    }
                }
            }
        }
        return movementMap
    }
}

private data class DijkstraElement(
    var turnUsed: Double = Turn.turn,
    var middleChunk: ChunkCoords? = null
) {
    val dijkstraMap: DijkstraMap = DijkstraMap()
    lateinit var map: Array<out CharArray>
    private val deleteAfter = 200.0


    fun getPath(from: Position, to: Position, impassable: Set<Coord>): List<Position> {
        val moveList = dijkstraMap.findPath(
            30, 40,
            impassable,
            null,
            positionToLocalCoord(from),
            positionToLocalCoord(to)
        )

        dijkstraMap.reset()
        dijkstraMap.clearGoals()

        return moveList.map { localCoordToPosition(it) }
    }

    private fun positionToLocalCoord(position: Position): Coord {
        val xOffset = compareValues(position.chunkCoords.x, middleChunk!!.x) * ChunkSize + ChunkSize
        val yOffset = compareValues(middleChunk!!.y, position.chunkCoords.y) * ChunkSize + ChunkSize

        return Coord.get(xOffset + position.x, yOffset + position.y)
    }

    private fun localCoordToPosition(coord: Coord): Position {
        val chunkXOffset = coord.x / ChunkSize - 1
        val chunkYOffset = 1 - coord.y / ChunkSize

        return Position(
            coord.x % ChunkSize,
            coord.y % ChunkSize,
            ChunkCoords(
                middleChunk!!.x + chunkXOffset,
                middleChunk!!.y + chunkYOffset,
                middleChunk!!.z
            )
        )
    }


    fun isIn(chunkCoords: ChunkCoords): Boolean {
        if (middleChunk == null) return false
        val isIn = abs(middleChunk!!.x - chunkCoords.x) <= 1 && abs(middleChunk!!.y - chunkCoords.y) <= 1
        if (!isIn && (Turn.turn - turnUsed).equalsDelta(deleteAfter)) {
            dijkstraMap.reset()
            turnUsed = 0.0
            middleChunk = null
        }
        return isIn
    }

    fun initializeWithMiddleChunk(
        middleChunk: ChunkCoords,
        chunkDijkstraMaps: MutableMap<ChunkCoords, Array<out CharArray>>
    ) {
        this.middleChunk = middleChunk
        map = create3x3DijkstraMap(middleChunk, chunkDijkstraMaps)
        // terrain cost can be easily added by calling the initializeCost method.
        dijkstraMap.measurement = Measurement.EUCLIDEAN
        dijkstraMap.initialize(map)
    }

    private fun create3x3DijkstraMap(
        middleChunk: ChunkCoords,
        chunkDijkstraMaps: MutableMap<ChunkCoords, Array<out CharArray>>,
        minX: Int = -1,
        minY: Int = -1,
        maxX: Int = 1,
        maxY: Int = 1
    ): Array<out CharArray> {
        val movementMap: Array<out CharArray> = Array(ChunkSize * 3) {CharArray(ChunkSize * 3) }
        for (cY in IntRange(minY, maxY).reversed()) {
            for (cX in minX .. maxX) {
                val dijkstraMap = chunkDijkstraMaps[ChunkCoords(middleChunk.x + cX, middleChunk.y + cY, middleChunk.z)]
                val cOffsetX = ChunkSize + ChunkSize * cX
                val cOffsetY = ChunkSize - ChunkSize * cY
                for (x in 0 until ChunkSize) {
                    for (y in 0 until ChunkSize) {
                        movementMap[cOffsetY + y][cOffsetX + x] = dijkstraMap?.get(y)?.get(x) ?: '#'
                    }
                }
            }
        }
        return movementMap
    }

    fun print() {
        for (i in map.indices) {
            for (j in map[i].indices) {
                print(map[i][j])
            }
            println()
        }
    }
}