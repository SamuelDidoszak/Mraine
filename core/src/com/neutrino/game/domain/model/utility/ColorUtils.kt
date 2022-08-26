package com.neutrino.game.domain.model.utility

import java.awt.Color
import kotlin.math.max
import kotlin.math.min

class ColorUtils {
    fun colorInterpolation(colorA: Color, colorB: Color, t: Int): Color {
        val r = colorA.red + (colorB.red - colorA.red) * t
        val g = colorA.green + (colorB.green - colorA.green) * t
        val b = colorA.blue + (colorB.blue - colorA.blue) * t
        return Color(r, g, b)
    }

    fun applySaturation(color: Color, saturation: Float): Color {
        val saturated = com.badlogic.gdx.graphics.Color().fromHsv(rgbToHue(color.red, color.green, color.blue), saturation, 1f)
        return Color(saturated.r, saturated.g, saturated.b)
    }

    fun rgbToHue(r: Int, g: Int, b: Int): Float {
        var h: Float = 0f
        var r = r / 255f
        var g = g / 255f
        var b = b / 255f
        val max: Float = max(r, max(g, b))
        val min: Float = min(r, min(g, b))

        when (max) {
            r -> {
                h = (g - b) / (max - min)
            } g -> {
            h = 2 + (b - r) / (max - min)
        } b -> {
            h = 4 + (r - g) / (max - min)
        }
        }

        h *= 60 // find the sector of 60 degrees to which the color belongs

        // make sure h is a positive angle on the color wheel between 0 and 360
        h %= 360
        if(h < 0){
            h += 360;
        }
        return h
    }

    fun toHexadecimal(color: Color): String {
        return String.format("#%02x%02x%02x", color.red, color.green, color.blue)
    }
}