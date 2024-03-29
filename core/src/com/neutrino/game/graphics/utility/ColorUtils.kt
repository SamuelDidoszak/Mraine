package com.neutrino.game.graphics.utility

import com.badlogic.gdx.graphics.Color
import com.neutrino.game.domain.model.systems.skills.SkillType
import kotlin.math.max
import kotlin.math.min

object ColorUtils {

    // Skills
    val STRENGTH: Color = Color.FIREBRICK
    val DEXTERITY: Color = Color.GOLDENROD
    val INTELLIGENCE: Color = Color.ROYAL
    val SUMMONING: Color = Color.MAROON

    // Requirements
    val REQ_MET: Color = Color.FOREST
    val REQ_UNMET: Color = applySaturation(Color.RED, 0.6f)

    // Items
    val GOLD: Color = Color.GOLD

    fun colorInterpolation(colorA: Color, colorB: Color, t: Int): Color {
        val r = colorA.r + (colorB.r - colorA.r) * t
        val g = colorA.g + (colorB.g - colorA.g) * t
        val b = colorA.b + (colorB.b - colorA.b) * t
        return Color(r, g, b, (colorA.a + colorB.a) / 2)
    }

    fun applySaturation(color: Color, saturation: Float): Color {
        return color.fromHsv(rgbToHue(color.r, color.g, color.b), saturation, 1f)
    }

    fun rgbToHue(r: Float, g: Float, b: Float): Float {
        var h: Float = 0f
        val r = r / 255f
        val g = g / 255f
        val b = b / 255f
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
        return String.format("#%02x%02x%02x", (color.r * 255).toInt(), (color.g * 255).toInt(), (color.b * 255).toInt())
    }

    fun Color.toHexaDecimal() = toHexadecimal(this)

    fun Color.toTextraColor() = "[" + toHexadecimal(this) + "]"

    fun getSkillTypeColor(skillType: SkillType): Color {
        return when (skillType) {
            SkillType.STRENGTH -> STRENGTH
            SkillType.DEXTERITY -> DEXTERITY
            SkillType.INTELLIGENCE -> INTELLIGENCE
            SkillType.SUMMONING -> SUMMONING
        }
    }
}