package com.neutrino.game.map.chunk

import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.util.Constants.ChunkSize
import squidpony.squidmath.Coord

class Impassables {

    private val impassables: MutableMap<ChunkCoords, MutableSet<Coord>> = mutableMapOf()
    private val regionImpassables: MutableMap<ChunkCoords, MutableSet<Coord>> = mutableMapOf()

    fun getByMiddleChunk(middleChunkCoords: ChunkCoords): MutableSet<Coord> {
        if (regionImpassables[middleChunkCoords] != null)
            return regionImpassables[middleChunkCoords]!!

        val regionImpassable: MutableSet<Coord> = mutableSetOf()

        for (x in -1 .. 1) {
            for (y in -1..1) {
                val chunkCoords = ChunkCoords(middleChunkCoords.x + x, middleChunkCoords.y + y, middleChunkCoords.z)
                impassables[chunkCoords]?.let { 
                    for (coord in it)
                        regionImpassable.add(coordToLocalCoord(coord, chunkCoords, middleChunkCoords))
                }
            }
        }

        regionImpassables[middleChunkCoords] = regionImpassable
        return regionImpassable
    }

    fun register(chunk: Chunk) {
        val coordList: MutableSet<Coord> = mutableSetOf()
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
                    if (entity has ChangesImpassable::class && !entity.get(MapParams::class)!!.allowCharacterOnTop) {
                        if (entity.get(Door::class)?.open == true)
                            continue

                        coordList.add(Coord.get(x, y))
                        break
                    }
                }
            }
        }
        impassables[chunk.chunkCoords] = coordList
        
        regionImpassables.forEach { 
            if (it.key.x in chunk.chunkCoords.x - 1 .. chunk.chunkCoords.x + 1 
                && it.key.y in chunk.chunkCoords.y - 1 .. chunk.chunkCoords.y + 1 
                && it.key.z == chunk.chunkCoords.z) {
                coordList.forEach { coord: Coord ->
                    it.value.add(coordToLocalCoord(coord, chunk.chunkCoords, it.key))
                }
            }
        }
    }

    fun unregister(chunk: Chunk) {
        val coordList = impassables[chunk.chunkCoords]
        impassables.remove(chunk.chunkCoords)

        if (coordList == null)
            return
        regionImpassables.forEach {
            if (it.key.x in chunk.chunkCoords.x - 1 .. chunk.chunkCoords.x + 1
                && it.key.y in chunk.chunkCoords.y - 1 .. chunk.chunkCoords.y + 1
                && it.key.z == chunk.chunkCoords.z) {
                coordList.forEach { coord: Coord ->
                    it.value.remove(coordToLocalCoord(coord, chunk.chunkCoords, it.key))
                }
            }
        }
    }

    fun update(position: Position, add: Boolean) {
        val chunkCoords = position.chunkCoords
        val coord = Coord.get(position.x, position.y)
        if (add)
            impassables[chunkCoords]?.add(coord)
        else
            impassables[chunkCoords]?.remove(coord)

        for (x in -1 .. 1) {
            for (y in -1 .. 1) {
                val middleChunkCoords = ChunkCoords(chunkCoords.x + x, chunkCoords.y + y, chunkCoords.z)
                val currentCoord = coordToLocalCoord(coord, position.chunkCoords, middleChunkCoords)
                if (add)
                    regionImpassables[middleChunkCoords]?.add(currentCoord)
                else
                    regionImpassables[middleChunkCoords]?.remove(currentCoord)
            }
        }
    }

    private fun coordToLocalCoord(coord: Coord, chunkCoords: ChunkCoords, middleChunk: ChunkCoords): Coord {
        val xOffset = (chunkCoords.x - middleChunk.x + 1) * ChunkSize
        val yOffset = (chunkCoords.y - middleChunk.y - 1) * ChunkSize

        return Coord.get(xOffset + coord.x, yOffset + coord.y)
    }
}