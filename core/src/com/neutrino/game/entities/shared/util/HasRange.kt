package com.neutrino.game.entities.shared.util

import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkManager
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
                        tiles.add(parsePosition(x, yPos, center.chunk))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(parsePosition(xPos, y, center.chunk))
                    }
                    tiles.remove(parsePosition(xPos, yPos, center.chunk))
                }
                RangeType.SQUARE -> {
                    for (y in yPos - range..yPos + range) {
                        for (x in xPos - range..xPos + range) {
                            tiles.add(parsePosition(x, y, center.chunk))
                        }
                    }
                }
                RangeType.CIRCLE -> {
                    var flipX = 1
                    var flipY = 1

                    for (i in 0 until 4) {
                        flipX = if (i % 2 == 0) 1 else -1
                        flipY = if (i < 2) 1 else -1

                        for (y in 1 until range) {
                            for (x in 1 until circleDistances[range][y]) {
                                tiles.add(parsePosition(xPos + flipX * x, yPos - flipY * y, center.chunk))
                            }
                        }
                    }
                    // Add diagonal lines
                    for (x in xPos - range .. xPos + range) {
                        tiles.add(parsePosition(x, yPos, center.chunk))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(parsePosition(xPos, y, center.chunk))
                    }
                    tiles.remove(parsePosition(xPos, yPos, center.chunk))
                }
            }
            if (omitCenter) {
                tiles.remove(center)
            }
            return tiles
        }

        private fun parsePosition(x: Int, y: Int, chunk: Chunk): Position {
            return ChunkManager.getCorrectPosition(Position(x, y, chunk))
        }

        fun isInRange(center: Position, target: Position, range: Int, rangeType: RangeType): Boolean {
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