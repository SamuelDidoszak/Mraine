package com.neutrino.game.utility

import com.badlogic.gdx.math.MathUtils.atan2
import com.badlogic.gdx.math.Vector2
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

object VectorOperations {
    /**
     * Returns the euclidean distance between source and destination
     */
    fun getDistance(sourceX: Float, sourceY: Float, destX: Float, destY: Float): Float {
        return sqrt((destX - sourceX).pow(2) + (destY - sourceY).pow(2))
    }

    /**
     * Returns length of a vector
     */
    fun getLength(x: Float, y: Float): Float {
        return sqrt(x.pow(2) + y.pow(2))
    }

    /**
     * Returns a normalized vector
     */
    fun normalize(x: Float, y: Float): Vector2 {
        val length = getLength(x, y)
        return Vector2(x / length, y / length)
    }

    /**
     * Returns the angle between two vectors
     */
    fun vectorAngle(sourceX: Float, sourceY: Float, destX: Float, destY: Float): Float {
        val sourceVector = normalize(sourceX, sourceY)
        val destVector = normalize(destX, destY)

        return acos(sourceVector.x * destVector.x + sourceVector.y * destVector.y)
    }

    /**
     * Returns the angle between two points
     */
    fun pointsAngle(sourceX: Float, sourceY: Float, destX: Float, destY: Float): Float {
        return atan2(destY - sourceY, destX - sourceX)
    }

    /**
     * Returns the angle between two points in degrees of a circle
     */
    fun pointsAngleDegrees(sourceX: Float, sourceY: Float, destX: Float, destY: Float): Float {
        return pointsAngle(sourceX, sourceY, destX, destY) * (180 / PI.toFloat())
    }
}