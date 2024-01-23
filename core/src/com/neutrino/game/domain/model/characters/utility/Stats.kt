package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType

interface Stats: HasRange {
    var hpMax: Float
    var hp: Float
    var mpMax: Float
    var mp: Float
    var strength: Float
    var dexterity: Float
    var intelligence: Float
    var luck: Float
    var damage: Float
    var damageVariation: Float
    var defence: Float
    /** Range is 0 - 1 which tells the probability of dodging */
    var evasion: Float
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    var accuracy: Float
    var criticalChance: Float
    /** Damage multiplier applied on critical hit */
    var criticalDamage: Float
    var attackSpeed: Double
    var movementSpeed: Double
    override var range: Int
    override var rangeType: RangeType
    var experience: Float
    var stealth: Float

    // elemental
    var fireDamage: Float
    var waterDamage: Float
    var earthDamage: Float
    var airDamage: Float
    var poisonDamage: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var fireDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var waterDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var earthDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var airDefence: Float
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var poisonDefence: Float

    fun damageMin(): Float {
        return damage - damageVariation
    }

    fun damageMax(): Float {
        return damage + damageVariation
    }

    fun damageRange(): ClosedFloatingPointRange<Float> {
        return damage - damageVariation .. damage + damageVariation
    }
}