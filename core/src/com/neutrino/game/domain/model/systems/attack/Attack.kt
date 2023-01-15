package com.neutrino.game.domain.model.systems.attack

import com.neutrino.LevelArrays
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
        StatsEnum.FIREDAMAGE to 0f,
        StatsEnum.WATERDAMAGE to 0f,
        StatsEnum.EARTHDAMAGE to 0f,
        StatsEnum.AIRDAMAGE to 0f,
        StatsEnum.POISONDAMAGE to 0f))

    abstract fun attack(character: Character, target: Coord)

    fun getTopmostAttackable(target: Coord): Attackable? {
        if (LevelArrays.getCharacterAt(target) is Attackable)
            return LevelArrays.getCharacterAt(target) as Attackable

        for (z in LevelArrays.getEntitiesAt(target).size .. 0) {
            if (LevelArrays.getEntitiesAt(target)[z] is Attackable)
                return LevelArrays.getEntitiesAt(target)[z] as Attackable
        }
        return null
    }

    fun getAllAttackable(target: Coord): List<Attackable> {
        val attackableList: ArrayList<Attackable> = ArrayList()
        if (LevelArrays.getCharacterAt(target) is Attackable)
            attackableList.add(LevelArrays.getCharacterAt(target) as Attackable)

        for (z in LevelArrays.getEntitiesAt(target).size .. 0) {
            if (LevelArrays.getEntitiesAt(target)[z] is Attackable)
                attackableList.add(LevelArrays.getEntitiesAt(target)[z] as Attackable)
        }
        return attackableList
    }
    fun getAttackData(character: Character): AttackData {
        return AttackData(
            if (acceptedDamageTypes.contains(StatsEnum.DAMAGE))
                character.damage + acceptedDamageTypes[StatsEnum.DAMAGE]!! - character.damageVariation + Random.nextFloat() * character.damageVariation else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.FIREDAMAGE)) character.fireDamage + acceptedDamageTypes[StatsEnum.FIREDAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.WATERDAMAGE)) character.waterDamage + acceptedDamageTypes[StatsEnum.WATERDAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.EARTHDAMAGE)) character.earthDamage + acceptedDamageTypes[StatsEnum.EARTHDAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.AIRDAMAGE)) character.airDamage + acceptedDamageTypes[StatsEnum.AIRDAMAGE]!! else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.POISONDAMAGE)) character.poisonDamage + acceptedDamageTypes[StatsEnum.POISONDAMAGE]!! else 0f,
            character.accuracy
        )
    }
}