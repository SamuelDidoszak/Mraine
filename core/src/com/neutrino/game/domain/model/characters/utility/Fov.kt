package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.Interactable
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.utility.BArray
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.round

/**
 * Recursive shadowcasting implementation of FOV
 * FOV is round with max distance 1 less than distance
 */
class Fov(var map: List<List<MutableList<Entity>>>) {

    companion object {
        val distance = HasRange.circleDistances

        /**
         * List of all octant transforms
         */
        val transforms: List<Transform> = listOf(
            Transform(1, 0, 0, 1),
            Transform(1, 0, 0, -1),
            Transform(-1, 0, 0, 1),
            Transform(-1, 0, 0, -1),
            Transform(0, 1, 1, 0),
            Transform(0, 1, -1, 0),
            Transform(0, -1, 1, 0),
            Transform(0, -1, -1, 0)
        )
    }

    /**
     * @param cx Center x position
     * @param cy Center y position
     * @param viewDistance View distance
     */
    fun updateFov(cx: Int, cy: Int, fov: Array<BooleanArray>, viewDistance: Int) {
        BArray.setFalse(fov)
        fov[cy][cx] = true

        /**
         * @param start left slope
         * @param end right slope
         */
        fun scan(y: Int, start: Double, end: Double, transform: Transform) {
            var start = start
            if (start.compareDelta(end) != -1)
                return

            if (y >= viewDistance)
                return

            val xMin = round((y - 0.5) * start).toInt()
            var xMax = min(ceil((y + 0.5) * end - 0.5).toInt(), distance[viewDistance][y])

            for (x in xMin .. xMax) {
                val realX = cx + transform.xx * x + transform.xy * y
                val realY = cy + transform.yx * x + transform.yy * y

                if (inMap(realX, realY) && transparent(realX, realY)) {
                    if (x >= y * start && x <= y * end)
                        fov[realY][realX] = true
                } else {
                    if (inMap(realX, realY) && x.toDouble().compareDelta((y - 0.5) * start) != -1  && (x - 0.5).compareDelta(y * end) != 1)
                        fov[realY][realX] = true

                    scan(y + 1, start, (x - 0.5) / y, transform)
                    start = (x + 0.5) / y
                    if (start >= end)
                        return
                }
            }
            scan(y + 1, start, end, transform)
        }

        for (i in 0 until 8)
            scan(1, 0.0, 1.0, transforms[i])
    }

    private fun inMap(x: Int, y: Int): Boolean {
        if (x !in 0 until map[0].size || y !in map.indices)
            return false
        return true
    }

    /**
     * Fetches the transparency of a tile.
     * Returns true if it's transparent, false otherwise
     */
    fun transparent(x: Int, y: Int): Boolean {
        for (entity in map[y][x]) {
            if (entity is DungeonWall ||
                (entity is Interactable && entity.getPrimaryInteraction() is InteractionType.DOOR))
                return false
        }
        return true
    }

    /**
     * Helper class holding transformation multipliers for each octant
     */
    data class Transform(
        val xx: Int,
        val xy: Int,
        val yx: Int,
        val yy: Int
    )

}