package com.neutrino.game.presentation.utility

import com.neutrino.game.equalsDelta
import kotlin.math.abs

class ValueComparison {
    private val equalsColor = "[WHITE]"
    private val lesserColor = "[RED]"
    private val biggerColor = "[GREEN]"

    /**
     * Compares both stats and returns adequate color as a string value formatted inside of [] for textraLabel's use
     * Accepts Double, Float, Int
     */
    fun compareStats(stat: Any, statOg: Any): String {
        if (stat is Double && statOg is Double) {
            if (stat.equalsDelta(statOg))
                return equalsColor
            when (stat.compareTo(statOg)) {
                -1 -> return lesserColor
                1 -> return biggerColor
            }
        }
        else if (stat is Float && statOg is Float) {
            if (stat.equalsDelta(statOg))
                return equalsColor
            when (stat.compareTo(statOg)) {
                -1 -> return lesserColor
                1 -> return biggerColor
            }
        }
        else if (stat is Int && statOg is Int) {
            when (stat.compareTo(statOg)) {
                0 -> return equalsColor
                -1 -> return lesserColor
                1 -> return biggerColor
            }
        }
        return "WHITE"
    }
}