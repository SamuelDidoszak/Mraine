package com.neutrino.game.map.generation.worldgen.biomes

import com.neutrino.game.util.Constants

data class BiomeCell(
    val x: Int,
    val y: Int,
    val id: Long,
    val seed: Long
) {
    fun getCellCenter(cell: BiomeCell): Pair<Int, Int> {
        val centerX = cell.x * Constants.BiomeCellSize + Constants.BiomeCellSize / 2
        val centerY = cell.y * Constants.BiomeCellSize + Constants.BiomeCellSize / 2
        return centerX to centerY
    }
}
