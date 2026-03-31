package com.neutrino.game.entities.shared.util

import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round

interface HasRange {
    var range: Int
    var rangeType: RangeType

    fun getTilesInRange(center: Position, omitCenter: Boolean = false): ArrayList<Position> {
        return getTilesInRange(center, range, rangeType, omitCenter)
    }

    fun isInRange(center: Position, target: Position): Boolean {
        return isInRange(center, target, range, rangeType)
    }

    companion object {
        fun getTilesInRange(center: Position, range: Int, rangeType: RangeType, omitCenter: Boolean = false): ArrayList<Position> {
            val tiles: ArrayList<Position> = ArrayList()
            val xPos = center.x
            val yPos = center.y
            when (rangeType) {
                RangeType.DIAGONAL -> {
                    for (x in xPos - range .. xPos + range) {
                        tiles.add(parsePosition(x, yPos, center.chunkCoords))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(parsePosition(xPos, y, center.chunkCoords))
                    }
                    tiles.remove(parsePosition(xPos, yPos, center.chunkCoords))
                }
                RangeType.SQUARE -> {
                    for (y in yPos - range..yPos + range) {
                        for (x in xPos - range..xPos + range) {
                            tiles.add(parsePosition(x, y, center.chunkCoords))
                        }
                    }
                }
                RangeType.CIRCLE -> {
                    var flipX: Int
                    var flipY: Int

                    for (i in 0 until 4) {
                        flipX = if (i % 2 == 0) 1 else -1
                        flipY = if (i < 2) 1 else -1

                        for (y in 1 until range) {
                            for (x in 1 until circleDistances[range][y]) {
                                tiles.add(parsePosition(xPos + flipX * x, yPos - flipY * y, center.chunkCoords))
                            }
                        }
                    }
                    // Add diagonal lines
                    for (x in xPos - range .. xPos + range) {
                        tiles.add(parsePosition(x, yPos, center.chunkCoords))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(parsePosition(xPos, y, center.chunkCoords))
                    }
                    tiles.remove(parsePosition(xPos, yPos, center.chunkCoords))
                }
            }
            if (omitCenter) {
                tiles.remove(center.getCorrectPosition())
            }
            return tiles
        }

        private fun parsePosition(x: Int, y: Int, chunkCoords: ChunkCoords): Position {
            return ChunkManager.getCorrectPosition(Position(x, y, chunkCoords))
        }

        fun isInRange(center: Position, target: Position, range: Int, rangeType: RangeType): Boolean {
            val center = center.toWorldTilePos()
            val target = target.toWorldTilePos()
            when (rangeType) {
                RangeType.DIAGONAL -> {
                    return (target.y == center.y &&
                                target.x in center.x - range .. center.x + range) ||
                            (target.x == center.x &&
                                    target.y in center.y - range .. center.y + range)
                }
                RangeType.SQUARE -> {
                    return (target.x in center.x - range .. center.x + range) &&
                            (target.y in center.y - range .. center.y + range)
                }
                RangeType.CIRCLE -> {
                    val xPos = center.x
                    val yPos = center.y

                    // Diagonal lines
                    if ((target.y == center.y &&
                        target.x in center.x - range .. center.x + range) ||
                        (target.x == center.x &&
                        target.y in center.y - range .. center.y + range))
                        return true

                    for (i in 0 until 2) {
                        val flipY = if (i % 2 == 0) 1 else -1

                        for (y in 1 until range) {
                            if (target.y != yPos - flipY * y)
                                continue

                            if (target.x in xPos - circleDistances[range][y] + 1 until xPos + circleDistances[range][y])
                                return true
                        }
                    }
                    return false
                }
            }
        }

        /**
         * Array of possible circle distances. Each distance is actually i - 1
         */
        val circleDistances: Array<IntArray> = Array(70) { IntArray(it) }

        init {
            for (maxDist in 1 until 70) {
                for (j in 1 .. maxDist) {
                    circleDistances[maxDist][j - 1] = round((maxDist + 0.05) * cos(asin(j / (maxDist + 0.5)))).toInt()
                }
            }
        }
    }
}