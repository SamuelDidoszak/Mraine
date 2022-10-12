package com.neutrino.game.domain.model.characters.utility

interface Stats {
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
    var dodging: Float
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    var accuracy: Float
    var criticalChance: Float
    var attackSpeed: Double
    var movementSpeed: Double
    var range: Int
    var rangeType: RangeType
    var experience: Float

    // environmental
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

    fun setDamage(): Float {
        return strength
    }

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