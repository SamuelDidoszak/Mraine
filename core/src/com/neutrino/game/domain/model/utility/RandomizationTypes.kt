package com.neutrino.game.domain.model.utility

import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.StatsEnum

enum class RandomizationTypes(val types: List<StatsEnum>) {
    /** Weapon without evasion or speed and with maxHp */
    HEAVYWEAPON(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGEVARIATION,
        StatsEnum.HPMAX,
        StatsEnum.ACCURACY,
        StatsEnum.CRITICALCHANCE,
        StatsEnum.CRITICALDAMAGE
    )),
    WEAPON(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGEVARIATION,
        StatsEnum.ACCURACY,
        StatsEnum.EVASION,
        StatsEnum.CRITICALCHANCE,
        StatsEnum.CRITICALDAMAGE,
        StatsEnum.ATTACKSPEED
    )),
    DEXWEAPON(listOf(
        StatsEnum.DEXTERITY,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGEVARIATION,
        StatsEnum.ACCURACY,
        StatsEnum.EVASION,
        StatsEnum.CRITICALCHANCE,
        StatsEnum.CRITICALDAMAGE,
        StatsEnum.ATTACKSPEED
    )),
    STAFF(listOf(
        StatsEnum.INTELLIGENCE,
        StatsEnum.LUCK,
        StatsEnum.DAMAGEVARIATION,
        StatsEnum.CRITICALCHANCE,
        StatsEnum.CRITICALDAMAGE
    )),
    DEXSTAFF(listOf(
        StatsEnum.INTELLIGENCE,
        StatsEnum.LUCK,
        StatsEnum.DAMAGEVARIATION,
        StatsEnum.EVASION,
        StatsEnum.CRITICALCHANCE,
        StatsEnum.CRITICALDAMAGE,
        StatsEnum.ATTACKSPEED
    )),
    ARMOR(listOf(
        StatsEnum.HPMAX,
        StatsEnum.DEFENCE,

    )),
    DEXARMOR(listOf(
        StatsEnum.HPMAX,
        StatsEnum.DEFENCE,
        StatsEnum.EVASION
    )),
    BOOTS(listOf(
        StatsEnum.HPMAX,
        StatsEnum.DEFENCE,
        StatsEnum.EVASION,
        StatsEnum.MOVEMENTSPEED
    )),


    // Types to join with
    BASESTATS(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.DEXTERITY,
        StatsEnum.INTELLIGENCE
    )),
    ELEMENTALDAMAGE(listOf(
        StatsEnum.FIREDAMAGE,
        StatsEnum.WATERDAMAGE,
        StatsEnum.EARTHDAMAGE,
        StatsEnum.AIRDAMAGE,
        StatsEnum.POISONDAMAGE
    )),
    ELEMENTALDEFENCE(listOf(
        StatsEnum.FIREDEFENCE,
        StatsEnum.WATERDEFENCE,
        StatsEnum.EARTHDEFENCE,
        StatsEnum.AIRDEFENCE,
        StatsEnum.POISONDEFENCE
    ));

    /** Returns a list with a random elementalDamage stat */
    fun typeWithElementalDamage(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(ELEMENTALDAMAGE.types[Constants.RandomGenerator.nextInt(ELEMENTALDAMAGE.types.size)])
    }

    /** Returns a list with a random elementalDefence stat */
    fun typeWithElementalDefence(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(ELEMENTALDEFENCE.types[Constants.RandomGenerator.nextInt(ELEMENTALDEFENCE.types.size)])
    }

    /** Returns a list with a random elementalDefence stat */
    fun typeWithBaseStat(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(BASESTATS.types[Constants.RandomGenerator.nextInt(BASESTATS.types.size)])
    }
}