package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.Constants
import kotlin.math.roundToInt

interface Randomization: Stats {
    val randomizationProbability: Float
    var randomizationMultiplier: Float
    /** Rounds the number to certain decimal places. 10 -> 0.1, 100 -> 0.01, 1000 -> 0.001 and so on */
    fun Double.roundToDecimalPlaces(decimalPlaces: Int): Double = (this * decimalPlaces).roundToInt() / decimalPlaces.toDouble()
    /** Rounds the number to certain decimal places. 10 -> 0.1, 100 -> 0.01, 1000 -> 0.001 and so on */
    fun Float.roundToDecimalPlaces(decimalPlaces: Int): Float = (this * decimalPlaces).roundToInt() / decimalPlaces.toFloat()

    private fun shouldRandomize(probability: Float = randomizationProbability): Boolean {return Constants.RandomGenerator.nextDouble() < probability}

    private fun getPrefferationTreshhold(threshold: Double): Double {
        return threshold.coerceIn(0.0, 1.0)
    }

    // Double
    fun Double.randomizeByPercent(change: Double, probability: Float = randomizationProbability): Double =
        if (!shouldRandomize(probability))
            this
        else {
            val randomizedValue = this + (this * (Constants.RandomGenerator.nextDouble(0.0, change * 2) - change))
            randomizationMultiplier += (randomizedValue / this - 1).toFloat()
            randomizedValue
        }

    fun Double.randomizeByValue(change: Double, probability: Float = randomizationProbability): Double =
        if (!shouldRandomize(probability))
            this
        else {
            val randomizedValue = this + (Constants.RandomGenerator.nextDouble(0.0, change * 2) - change)
            randomizationMultiplier += (randomizedValue / this - 1).toFloat()
            randomizedValue
        }

    // Float
    fun Float.randomizeByPercent(change: Float, probability: Float = randomizationProbability): Float =
        if (!shouldRandomize(probability))
            this
        else {
            val randomizedValue = this + (this * (Constants.RandomGenerator.nextDouble(0.0, change * 2.0) - change)).toFloat()
            randomizationMultiplier += (randomizedValue / this - 1)
            randomizedValue
        }

    fun Float.randomizeByValue(change: Float, probability: Float = randomizationProbability): Float =
        if (!shouldRandomize(probability))
            this
        else {
            val randomizedValue = this + (Constants.RandomGenerator.nextDouble(0.0, change * 2.0) - change).toFloat()
            randomizationMultiplier += (randomizedValue / this - 1)
            randomizedValue
        }
}