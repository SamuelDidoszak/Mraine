package com.neutrino.game.domain.model.characters.utility

import squidpony.squidmath.Coord

interface HasRange {
    var range: Int
    var rangeType: RangeType

    fun getTilesInRange(center: Coord): ArrayList<Coord> {
        return getTilesInRange(center, range, rangeType)
    }

    companion object {
        fun getTilesInRange(center: Coord, range: Int, rangeType: RangeType): ArrayList<Coord> {
            val tiles: ArrayList<Coord> = ArrayList()
            val xPos = center.x
            val yPos = center.y
            when (rangeType) {
                RangeType.DIAGONAL -> {
                    for (x in xPos - range..xPos + range) {
                        tiles.add(Coord.get(x, yPos))
                    }
                    for (y in yPos - range..xPos + range) {
                        tiles.add(Coord.get(xPos, y))
                    }
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
            return tiles
        }
    }
}