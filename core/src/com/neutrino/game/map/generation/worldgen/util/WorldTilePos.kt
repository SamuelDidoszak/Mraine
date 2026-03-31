package com.neutrino.game.map.generation.worldgen.util

import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.util.Constants.ChunkSize

data class WorldTilePos(
    val x:Int,
    val y:Int,
    val chunkZ:Int
) {

    fun toPosition(): Position = Position(x, y, ChunkCoords(0, 0, chunkZ)).getCorrectPosition()

    companion object {
        fun from(position: Position): WorldTilePos {
            return WorldTilePos(
                position.chunkCoords.x * ChunkSize + position.x,
                -1 * position.chunkCoords.y * ChunkSize + position.y,
                position.chunkCoords.z
            )
        }
    }
}
