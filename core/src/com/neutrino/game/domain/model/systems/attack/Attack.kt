package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.utility.AttackData
import com.neutrino.game.domain.model.systems.attack.utility.Attackable
import squidpony.squidmath.Coord
import kotlin.random.Random


abstract class Attack(
    val acceptedDamageTypes: Map<StatsEnum, Float>
) {
    /**
     * Creates an attack which accepts all the damage types
     */
    constructor(): this(mapOf(
        StatsEnum.DAMAGE to 0f,
        StatsEnum.FIRE_DAMAGE to 0f,
        StatsEnum.WATER_DAMAGE to 0f,
        StatsEnum.EARTH_DAMAGE to 0f,
        StatsEnum.AIR_DAMAGE to 0f,
        StatsEnum.POISON_DAMAGE to 0f))

    abstract fun attack(character: Character, target: Coord)

    fun getTopmostAttackable(target: Coord): Attackable? {
        // TODO OLD Attack
//        if (LevelArrays.getCharacterAt(target) is Attackable)
//            return LevelArrays.getCharacterAt(target) as Attackable

        // TODO OLD Attack
//        for (z in LevelArrays.getEntitiesAt(target).size - 1 downTo 0) {
//            if (LevelArrays.getEntitiesAt(target)[z] is Attackable)
//                return LevelArrays.getEntitiesAt(target)[z] as Attackable
//        }
        return null
    }

    fun getAllAttackable(target: Coord): List<Attackable> {
        val attackableList: ArrayList<Attackable> = ArrayList()
        // TODO OLD Attack
//        if (LevelArrays.getCharacterAt(target) is Attackable)
//            attackableList.add(LevelArrays.getCharacterAt(target) as Attackable)

        // TODO OLD Attack
//        for (z in LevelArrays.getEntitiesAt(target).size - 1 downTo 0) {
//            if (LevelArrays.getEntitiesAt(target)[z] is Attackable)
//                attackableList.add(LevelArrays.getEntitiesAt(target)[z] as Attackable)
//        }
        return attackableList
    }
    fun getAttackData(character: Character): AttackData {
        return AttackData(
            if (acceptedDamageTypes.contains(StatsEnum.DAMAGE))
                character.damage + acceptedDamageTypes[StatsEnum.DAMAGE]!! - character.damageVariation + Random.nextFloat() * character.damageVariation else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.FIRE_DAMAGE)) character.fireDamage + acceptedDamageTypes[StatsEnum.FIRE_DAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.WATER_DAMAGE)) character.waterDamage + acceptedDamageTypes[StatsEnum.WATER_DAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.EARTH_DAMAGE)) character.earthDamage + acceptedDamageTypes[StatsEnum.EARTH_DAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.AIR_DAMAGE)) character.airDamage + acceptedDamageTypes[StatsEnum.AIR_DAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.POISON_DAMAGE)) character.poisonDamage + acceptedDamageTypes[StatsEnum.POISON_DAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.CRITICAL_CHANCE)) character.criticalChance + acceptedDamageTypes[StatsEnum.CRITICAL_CHANCE]!! else character.criticalChance,
            if (acceptedDamageTypes.contains(StatsEnum.CRITICAL_DAMAGE)) character.criticalDamage + acceptedDamageTypes[StatsEnum.CRITICAL_DAMAGE]!! else character.criticalDamage,
            character.accuracy,

            character = character
        )
    }
}