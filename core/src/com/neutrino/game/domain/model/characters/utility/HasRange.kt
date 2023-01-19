package com.neutrino.game.domain.model.characters.utility

import squidpony.squidmath.Coord
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.round

interface HasRange {
    var range: Int
    var rangeType: RangeType

    fun getTilesInRange(center: Coord, omitCenter: Boolean = false): ArrayList<Coord> {
        return getTilesInRange(center, range, rangeType, omitCenter)
    }

    fun isInRange(center: Coord, target: Coord): Boolean {
        return Companion.isInRange(center, target, range, rangeType)
    }

    companion object {
        fun getTilesInRange(center: Coord, range: Int, rangeType: RangeType, omitCenter: Boolean = false): ArrayList<Coord> {
            val tiles: ArrayList<Coord> = ArrayList()
            val xPos = center.x
            val yPos = center.y
            when (rangeType) {
                RangeType.DIAGONAL -> {
                    for (x in xPos - range .. xPos + range) {
                        tiles.add(Coord.get(x, yPos))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(Coord.get(xPos, y))
                    }
                    tiles.remove(Coord.get(xPos, yPos))
                }
                RangeType.SQUARE -> {
                    for (y in yPos - range..yPos + range) {
                        for (x in xPos - range..xPos + range) {
                            tiles.add(Coord.get(x, y))
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
                                tiles.add(Coord.get(xPos + flipX * x, yPos - flipY * y))
                            }
                        }
                    }
                    // Add diagonal lines
                    for (x in xPos - range .. xPos + range) {
                        tiles.add(Coord.get(x, yPos))
                    }
                    for (y in yPos - range .. yPos + range) {
                        tiles.add(Coord.get(xPos, y))
                    }
                    tiles.remove(Coord.get(xPos, yPos))
                }
            }
            if (omitCenter) {
                tiles.remove(center)
            }
            return tiles
        }

        fun isInRange(center: Coord, target: Coord, range: Int, rangeType: RangeType): Boolean {
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