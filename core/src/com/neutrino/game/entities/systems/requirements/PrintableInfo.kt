package com.neutrino.game.entities.systems.requirements

import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.graphics.utility.ColorUtils.toHexaDecimal

interface PrintableInfo<T: Any> {

    fun getPrintableInfo(other: T?): List<Pair<String, Any?>>

    companion object {
        val betterColor = "[${ColorUtils.REQ_MET.toHexaDecimal()}]"
        val baseColor = "[${ColorUtils.BLACK.toHexaDecimal()}]"
        val worseColor = "[${ColorUtils.REQ_UNMET.toHexaDecimal()}]"

        fun getColor(sign: Int): String {
            return when (sign) {
                -1 -> worseColor
                0 -> baseColor
                else -> betterColor
            }
        }
    }
}