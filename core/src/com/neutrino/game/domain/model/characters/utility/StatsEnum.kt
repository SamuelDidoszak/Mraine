package com.neutrino.game.domain.model.characters.utility

enum class StatsEnum(val priorityWeight: Float, val statCost: Float) {
    HP_MAX(5f, 1.25f),
    MP_MAX(5f, 1f),
    STRENGTH(3.5f, 2.25f),
    DEXTERITY(3.5f, 2.25f),
    INTELLIGENCE(3.5f, 2.25f),
    LUCK(3.5f, 2.25f),
    DAMAGE(5f,1.25f),
    DAMAGE_VARIATION(3.5f, 1.5f),
    DEFENCE(5f, 1.25f),
    EVASION(1.5f, 1.5f),
    ACCURACY(1.5f, 1f),
    CRITICAL_CHANCE(5f, 1.25f),
    CRITICAL_DAMAGE(5f, 1.25f),
    ATTACK_SPEED(1.5f, 3f),
    MOVEMENT_SPEED(1.5f, 3f),
    RANGE(0f, 10f),
    RANGE_TYPE(0f, 10f),
    STEALTH(1.5f, 1.25f),

    FIRE_DAMAGE(3.5f, 1.5f),
    WATER_DAMAGE(3.5f, 1.5f),
    EARTH_DAMAGE(3.5f, 1.5f),
    AIR_DAMAGE(3.5f, 1.5f),
    POISON_DAMAGE(3.5f, 1.5f),

    FIRE_DEFENCE(3.5f, 1.25f),
    WATER_DEFENCE(3.5f, 1.25f),
    EARTH_DEFENCE(3.5f, 1.25f),
    AIR_DEFENCE(3.5f, 1.25f),
    POISON_DEFENCE(3.5f, 1.25f);

    override fun toString(): String {
        return super.toString().replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() }
    }
}