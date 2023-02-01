package com.neutrino.game.domain.model.utility

import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.utility.StatsEnum

enum class RandomizationTypes(val types: List<StatsEnum>) {
    /** Weapon without evasion or speed and with maxHp */
    HEAVYWEAPON(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGE_VARIATION,
        StatsEnum.HP_MAX,
        StatsEnum.ACCURACY,
        StatsEnum.CRITICAL_CHANCE,
        StatsEnum.CRITICAL_DAMAGE
    )),
    WEAPON(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGE_VARIATION,
        StatsEnum.ACCURACY,
        StatsEnum.EVASION,
        StatsEnum.CRITICAL_CHANCE,
        StatsEnum.CRITICAL_DAMAGE,
        StatsEnum.ATTACK_SPEED
    )),
    DEXWEAPON(listOf(
        StatsEnum.DEXTERITY,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE,
        StatsEnum.DAMAGE_VARIATION,
        StatsEnum.ACCURACY,
        StatsEnum.EVASION,
        StatsEnum.CRITICAL_CHANCE,
        StatsEnum.CRITICAL_DAMAGE,
        StatsEnum.ATTACK_SPEED
    )),
    STAFF(listOf(
        StatsEnum.INTELLIGENCE,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE_VARIATION,
        StatsEnum.CRITICAL_CHANCE,
        StatsEnum.CRITICAL_DAMAGE
    )),
    DEXSTAFF(listOf(
        StatsEnum.INTELLIGENCE,
        StatsEnum.LUCK,
        StatsEnum.DAMAGE_VARIATION,
        StatsEnum.EVASION,
        StatsEnum.CRITICAL_CHANCE,
        StatsEnum.CRITICAL_DAMAGE,
        StatsEnum.ATTACK_SPEED
    )),
    ARMOR(listOf(
        StatsEnum.HP_MAX,
        StatsEnum.DEFENCE,

    )),
    DEXARMOR(listOf(
        StatsEnum.HP_MAX,
        StatsEnum.DEFENCE,
        StatsEnum.EVASION
    )),
    BOOTS(listOf(
        StatsEnum.HP_MAX,
        StatsEnum.DEFENCE,
        StatsEnum.EVASION,
        StatsEnum.MOVEMENT_SPEED
    )),


    // Types to join with
    BASESTATS(listOf(
        StatsEnum.STRENGTH,
        StatsEnum.DEXTERITY,
        StatsEnum.INTELLIGENCE
    )),
    ELEMENTALDAMAGE(listOf(
        StatsEnum.FIRE_DAMAGE,
        StatsEnum.WATER_DAMAGE,
        StatsEnum.EARTH_DAMAGE,
        StatsEnum.AIR_DAMAGE,
        StatsEnum.POISON_DAMAGE
    )),
    ELEMENTALDEFENCE(listOf(
        StatsEnum.FIRE_DEFENCE,
        StatsEnum.WATER_DEFENCE,
        StatsEnum.EARTH_DEFENCE,
        StatsEnum.AIR_DEFENCE,
        StatsEnum.POISON_DEFENCE
    ));

    /** Returns a list with a random elementalDamage stat */
    fun typeWithElementalDamage(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(ELEMENTALDAMAGE.types[LevelArrays.getLevel().randomGenerator.nextInt(ELEMENTALDAMAGE.types.size)])
    }

    /** Returns a list with a random elementalDefence stat */
    fun typeWithElementalDefence(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(ELEMENTALDEFENCE.types[LevelArrays.getLevel().randomGenerator.nextInt(ELEMENTALDEFENCE.types.size)])
    }

    /** Returns a list with a random elementalDefence stat */
    fun typeWithBaseStat(type: RandomizationTypes): List<StatsEnum> {
        return type.types.plus(BASESTATS.types[LevelArrays.getLevel().randomGenerator.nextInt(BASESTATS.types.size)])
    }
}