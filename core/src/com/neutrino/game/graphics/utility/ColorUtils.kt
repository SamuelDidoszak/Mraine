package com.neutrino.game.graphics.utility

import com.badlogic.gdx.graphics.Color
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.entities.systems.skills.SkillType
import kotlin.math.max
import kotlin.math.min

object ColorUtils {

    // Stats
    val hp: Color = Color.valueOf("3db01e")
    val mp: Color = Color.valueOf("2725c9")
    val strength: Color = Color.valueOf("a81c1c")
    val dexterity: Color = Color.valueOf("1b6627")
    val intelligence: Color = Color.valueOf("2725c9")
    val luck: Color = Color.valueOf("dfa017")
    val damage: Color = strength
    val defence: Color = Color.valueOf("1a8a99")
    val evasion: Color = dexterity
    val stealth: Color = dexterity
    val accuracy: Color = dexterity
    val criticalChance: Color = luck
    val criticalDamage: Color = luck
    val movementSpeed: Color = intelligence
    val attackSpeed: Color = strength
    val fireDamage: Color = Color.valueOf("e86a1d")
    val fireDefence: Color = fireDamage
    val waterDamage: Color = Color.valueOf("2554da")
    val waterDefence: Color = waterDamage
    val airDamage: Color = Color.valueOf("3cbec0")
    val airDefence: Color = airDamage
    val poisonDamage: Color = Color.valueOf("70951b")
    val poisonDefence: Color = poisonDamage
    val range: Color = dexterity
    val rangeType: Color = dexterity

    val cooldown: Color = mp

    // Skills
    val SKILL_STRENGTH: Color = Color.FIREBRICK
    val SKILL_DEXTERITY: Color = Color.GOLDENROD
    val SKILL_INTELLIGENCE: Color = Color.ROYAL
    val SKILL_SUMMONING: Color = Color.MAROON

    // Requirements
    val BLACK: Color = Color(0f, 0f, 0f, 1f)
    val REQ_MET: Color = Color.FOREST
    val REQ_UNMET: Color = applySaturation(Color.RED, 0.7f)

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
            SkillType.STRENGTH -> SKILL_STRENGTH
            SkillType.DEXTERITY -> SKILL_DEXTERITY
            SkillType.INTELLIGENCE -> SKILL_INTELLIGENCE
            SkillType.SUMMONING -> SKILL_SUMMONING
        }
    }

    fun getStatColorTextra(statEnum: StatsEnum): String = getStatColorTextra(statEnum.toString())
    fun getStatColorTextra(statName: String): String = getStatColor(statName).toTextraColor()

    fun getStatColor(statEnum: StatsEnum): Color = getStatColor(statEnum.toString())
    fun getStatColor(statName: String): Color {
        return when (statName.lowercase()) {
            "Hp".lowercase() -> hp
            "Mp".lowercase() -> mp
            "Strength".lowercase() -> strength
            "Dexterity".lowercase() -> dexterity
            "Intelligence".lowercase() -> intelligence
            "Luck".lowercase() -> luck
            "Defence".lowercase() -> defence
            "Damage".lowercase() -> damage
            "DamageMin".lowercase() -> damage
            "DamageMax".lowercase() -> damage
            "Accuracy".lowercase() -> accuracy
            "CriticalChance".lowercase() -> criticalChance
            "CriticalDamage".lowercase() -> criticalDamage
            "Evasion".lowercase() -> evasion
            "Stealth".lowercase() -> stealth
            "MovementSpeed".lowercase() -> movementSpeed
            "AttackSpeed".lowercase() -> attackSpeed
            "Range".lowercase() -> range
            "RangeType".lowercase() -> rangeType
            "FireDamage".lowercase() -> fireDamage
            "FireDamageMin".lowercase() -> fireDamage
            "FireDamageMax".lowercase() -> fireDamage
            "FireDefence".lowercase() -> fireDefence
            "WaterDamage".lowercase() -> waterDamage
            "WaterDamageMin".lowercase() -> waterDamage
            "WaterDamageMax".lowercase() -> waterDamage
            "WaterDefence".lowercase() -> waterDefence
            "AirDamage".lowercase() -> airDamage
            "AirDamageMin".lowercase() -> airDamage
            "AirDamageMax".lowercase() -> airDamage
            "AirDefence".lowercase() -> airDefence
            "PoisonDamage".lowercase() -> poisonDamage
            "PoisonDamageMin".lowercase() -> poisonDamage
            "PoisonDamageMax".lowercase() -> poisonDamage
            "PoisonDefence".lowercase() -> poisonDefence
            "Cooldown".lowercase() -> cooldown
            else -> BLACK
        }
    }
}