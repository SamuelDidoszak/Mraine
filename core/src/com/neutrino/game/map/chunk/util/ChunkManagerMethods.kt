package com.neutrino.game.map.chunk.util

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkManager.middleChunk
import com.neutrino.game.util.Constants
import squidpony.squidmath.Coord

interface ChunkManagerMethods {

    fun convertPositionToChunkMapPosition(position: Position): Coord {
        val offset = getChunkOffset(position.chunk)
        return Coord.get(position.x + (offset.first + 1) * Constants.LevelChunkSize,
            position.y + (offset.first + 1) * Constants.LevelChunkSize)
    }

    fun getChunkOffset(chunk: Chunk): Triple<Int, Int, Int> {
        return Triple(chunk.chunkCoords.x - middleChunk.chunkCoords.x,
            chunk.chunkCoords.y - middleChunk.chunkCoords.y,
            chunk.chunkCoords.z - middleChunk.chunkCoords.z)
    }

    fun getCharacterAt(coord: Coord): Entity? {
        return getCharacterAt(Position(coord.x, coord.y, middleChunk))
    }

    fun getCharacterAt(position: Position): Entity? {
        return position.chunk.characterMap[position.y][position.x]
    }

    fun allowsCharacter(position: Position): Boolean {
        var allow = true
        for (entity in position.chunk.map[position.y][position.x]) {
            if (!entity.get(MapParams::class)!!.allowCharacterOnTop) {
                allow = false
                break
            }
        }
        return allow
    }

    fun allowsCharacterChangesImpassable(position: Position): Boolean {
        var allow = true
        for (entity in position.chunk.map[position.y][position.x]) {
            if (!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                allow = false
                break
            }
        }
        return allow
    }
}