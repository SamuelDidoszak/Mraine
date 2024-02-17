package com.neutrino

import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.chunk.Chunk
import squidpony.squidmath.Coord

object ChunkManager {

    private val chunkDrawerMap: HashMap<Chunk, LevelDrawer> = HashMap()

    fun addChunk(chunk: Chunk, levelDrawer: LevelDrawer) {
        chunkDrawerMap[chunk] = levelDrawer
    }

    fun getDrawer(chunk: Chunk): LevelDrawer {
        return chunkDrawerMap[chunk]!!
    }

    fun getPath(entity: Entity, position: Position): List<Coord> {
        val entityPosition = entity.get(Position::class)!!
        val moveList = entityPosition.chunk.dijkstraMap.findPath(
            30, 30,  entityPosition.chunk.getImpassable(), null,
            entityPosition.getPosition(), position.getPosition())
        entityPosition.chunk.dijkstraMap.reset()
//        entityPosition.chunk.dijkstraMap.clearGoals()
        return moveList
        // TODO MULTIPLE CHUNKS Make this method return List<Position>
//        return moveList.map { Position(it, entityPosition.chunk) }
    }

    fun getCharacterAt(coord: Coord): Entity? {
        return getCharacterAt(coord.x, coord.y)
    }

    fun getCharacterAt(x: Int, y: Int): Entity? {
        return Turn.currentChunk.characterMap[y][x]
    }


}