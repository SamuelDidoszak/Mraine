package com.neutrino.game.UI.popups

import com.neutrino.game.equalsDelta
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor

class ValueComparison {
    private companion object {
        private val equalsColor = "[BLACK]"
        private val lesserColor = ColorUtils.REQ_UNMET.toTextraColor()
        private val biggerColor = ColorUtils.REQ_MET.toTextraColor()
    }

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
        return "[BLACK]"
    }
}