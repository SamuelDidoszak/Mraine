package com.neutrino

import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.level.Chunk

object ChunkManager {

    private val chunkDrawerMap: HashMap<Chunk, LevelDrawer> = HashMap()

    fun addChunk(chunk: Chunk, levelDrawer: LevelDrawer) {
        chunkDrawerMap[chunk] = levelDrawer
    }

    fun getDrawer(chunk: Chunk): LevelDrawer {
        return chunkDrawerMap[chunk]!!
    }
}