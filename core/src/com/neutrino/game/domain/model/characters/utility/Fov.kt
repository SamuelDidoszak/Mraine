package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.entities.utility.Interactable
import com.neutrino.game.domain.model.entities.utility.Interaction
import com.neutrino.game.domain.model.map.Map
import com.neutrino.game.utility.BArray
import kotlin.math.*

/**
 * Recursive shadowcasting implementation of FOV
 * FOV is round with max distance 1 less than distance
 */
class Fov(var map: Map) {

    companion object {
        // TODO when implementing several distance values, make a precalculated indexed array of distances
        /**
         * View distance of FOV. Actually is i - 1
         */
        const val viewDistance = 20
        val distance: IntArray = IntArray(viewDistance)

        init {
            for (j in 1 .. viewDistance) {
                distance[j - 1] = round((viewDistance + 0.05) * cos(asin(j / (viewDistance + 0.5)))).toInt()
            }
        }

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
     */
    fun updateFov(cx: Int, cy: Int, fov: Array<BooleanArray>) {
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
            var xMax = min(ceil((y + 0.5) * end - 0.5).toInt(), distance[y])

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
        if (x !in 0 until map.xMax || y !in 0 until map.yMax)
            return false
        return true
    }

    /**
     * Fetches the transparency of a tile.
     * Returns true if it's transparent, false otherwise
     */
    fun transparent(x: Int, y: Int): Boolean {
        for (entity in map.map[y][x]) {
            if (entity is DungeonWall ||
                (entity is Interactable && entity.getPrimaryInteraction() is Interaction.DOOR))
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