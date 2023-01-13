package com.neutrino.game.domain.model.systems.attack

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.utility.AttackData
import com.neutrino.game.domain.model.systems.attack.utility.Attackable
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord
import kotlin.random.Random

abstract class Attack(
    val acceptedDamageTypes: Set<StatsEnum>
) {
    /**
     * Creates an attack which accepts all the damage types
     */
    constructor(): this(setOf(StatsEnum.DAMAGE, StatsEnum.FIREDAMAGE, StatsEnum.WATERDAMAGE, StatsEnum.EARTHDAMAGE, StatsEnum.AIRDAMAGE, StatsEnum.POISONDAMAGE))

    abstract fun attack(character: Character, target: Coord)

    fun getTopmostAttackable(target: Coord): Attackable? {
        if (Turn.currentLevel.characterMap[target.y][target.x] is Attackable)
            return Turn.currentLevel.characterMap[target.y][target.x] as Attackable

        for (z in Turn.currentLevel.map.map[target.y][target.x].size .. 0) {
            if (Turn.currentLevel.map.map[target.y][target.x][z] is Attackable)
                return Turn.currentLevel.map.map[target.y][target.x][z] as Attackable
        }
        return null
    }

    fun getAttackData(character: Character): AttackData {
        return AttackData(
            if (acceptedDamageTypes.contains(StatsEnum.DAMAGE)) character.damage - character.damageVariation + Random.nextFloat() * character.damageVariation else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.FIREDAMAGE)) character.fireDamage else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.WATERDAMAGE)) character.waterDamage else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.EARTHDAMAGE)) character.earthDamage else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.AIRDAMAGE)) character.airDamage else 0f,
            if (acceptedDamageTypes.contains(StatsEnum.POISONDAMAGE)) character.poisonDamage else 0f,
            character.accuracy
        )
    }
}