package com.neutrino.game.utility

import kotlin.random.Random

class ProbabilityList<T: Any>(
    vararg val probabilityList: Probability<T>
) {

    fun resolve(rng: Random): T? {
        val total = probabilityList.sumOf { it.probability.toDouble() }.toFloat()
        if (total <= 0f) return null

        var roll = rng.nextFloat() * total

        for ((value, probability) in probabilityList) {
            roll -= probability
            if (roll <= 0f) return value
        }

        return null
    }
}