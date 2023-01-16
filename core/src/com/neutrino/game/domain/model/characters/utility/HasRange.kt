package com.neutrino.game.domain.model.characters.utility

import squidpony.squidmath.Coord

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
                RangeType.DIAMOND -> {
                    TODO("implement DIAMOND RANGE TYPE")

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
                RangeType.DIAMOND -> {
                    TODO("implement DIAMOND RANGE TYPE")

                }
            }
        }
    }
}