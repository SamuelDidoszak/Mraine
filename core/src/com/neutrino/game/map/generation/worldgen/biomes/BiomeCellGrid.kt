package com.neutrino.game.map.generation.worldgen.biomes

import com.neutrino.game.util.Constants
import java.lang.Math.floorDiv

class BiomeCellGrid(
    private val worldSeed: Long
) {

    fun cellAt(x: Int, y: Int): BiomeCell {
        val cellX = floorDiv(x, Constants.BiomeCellSize)
        val cellY = floorDiv(y, Constants.BiomeCellSize)

        val id = computeCellId(cellX, cellY)
        val seed = computeCellSeed(cellX, cellY)

        return BiomeCell(
            x = cellX,
            y = cellY,
            id = id,
            seed = seed
        )
    }

    private fun computeCellId(cellX: Int, cellY: Int): Long {
        return (cellX.toLong() shl 32) xor (cellY.toLong() and 0xffffffffL)
    }

    private fun computeCellSeed(cellX: Int, cellY: Int): Long {
        var result = worldSeed
        result = result * 31L + cellX
        result = result * 31L + cellY
        return result
    }

    fun getChunkCells(chunkX: Int, chunkY: Int): List<List<BiomeCell>> {
        val cellsX = Constants.ChunkSize / Constants.BiomeCellSize
        val cellsY = Constants.ChunkSize / Constants.BiomeCellSize
        val cells = Array(cellsX) { ArrayList<BiomeCell>(cellsY) }

        for (cx in 0 until cellsX) {
            for (cy in 0 until cellsY) {

                val worldX = chunkX + cx * Constants.BiomeCellSize
                val worldY = chunkY + cy * Constants.BiomeCellSize

                cells[cx].add(cellAt(worldX, worldY))
            }
        }
        return cells.map { it.toList() }
    }
}
