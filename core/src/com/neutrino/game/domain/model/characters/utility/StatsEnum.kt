package com.neutrino.game.domain.model.characters.utility

enum class StatsEnum(val priorityWeight: Float, val statCost: Float) {
    HPMAX(5f, 1.25f),
    MPMAX(5f, 1f),
    STRENGTH(3.5f, 2.25f),
    DEXTERITY(3.5f, 2.25f),
    INTELLIGENCE(3.5f, 2.25f),
    LUCK(3.5f, 2.25f),
    DAMAGE(5f,1.25f),
    DAMAGEVARIATION(3.5f, 1.5f),
    DEFENCE(5f, 1.25f),
    EVASION(1.5f, 1.5f),
    ACCURACY(1.5f, 1f),
    CRITICALCHANCE(5f, 1.25f),
    CRITICALDAMAGE(5f, 1.25f),
    ATTACKSPEED(1.5f, 3f),
    MOVEMENTSPEED(1.5f, 3f),
    RANGE(0f, 10f),
    RANGETYPE(0f, 10f),

    FIREDAMAGE(3.5f, 1.5f),
    WATERDAMAGE(3.5f, 1.5f),
    EARTHDAMAGE(3.5f, 1.5f),
    AIRDAMAGE(3.5f, 1.5f),
    POISONDAMAGE(3.5f, 1.5f),

    FIREDEFENCE(3.5f, 1.25f),
    WATERDEFENCE(3.5f, 1.25f),
    EARTHDEFENCE(3.5f, 1.25f),
    AIRDEFENCE(3.5f, 1.25f),
    POISONDEFENCE(3.5f, 1.25f);

    override fun toString(): String {
        return super.toString().lowercase()
    }
}