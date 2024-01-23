package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute

class DefensiveStats(
    var hpMax: Float = 1f,
    var hp: Float = hpMax,
    var mpMax: Float = 0f,
    var mp: Float = 0f,
    var defence: Float = 0f,
    /** Range is 0 - 1 which tells the probability of dodging */
    var evasion: Float = 0f,
    var movementSpeed: Double = 1.0,
    var stealth: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var fireDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var waterDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var airDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var poisonDefence: Float = 0f
): Attribute()