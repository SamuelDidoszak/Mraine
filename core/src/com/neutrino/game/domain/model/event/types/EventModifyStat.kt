package com.neutrino.game.domain.model.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.Event

class EventModifyStat(
    val stat: StatsEnum,
    var value: Any,
    val percent: Boolean = false
): Event<Character> {
    override lateinit var data: Character
    override var dataAttached: Boolean = false

    override fun start() {
        checkData()
        
        val character = data
        when (stat) {
            StatsEnum.HPMAX -> if (!percent) character.hpMax += value as Float else character.hpMax *= value as Float
            StatsEnum.MPMAX -> if (!percent) character.mpMax += value as Float else character.mpMax *= value as Float
            StatsEnum.STRENGTH -> if (!percent) character.strength += value as Float else character.strength *= value as Float
            StatsEnum.DEXTERITY -> if (!percent) character.dexterity += value as Float else character.dexterity *= value as Float
            StatsEnum.INTELLIGENCE -> if (!percent) character.intelligence += value as Float else character.intelligence *= value as Float
            StatsEnum.LUCK -> if (!percent) character.luck += value as Float else character.luck *= value as Float
            StatsEnum.DAMAGE -> if (!percent) character.damage += value as Float else character.damage *= value as Float
            StatsEnum.DAMAGEVARIATION -> if (!percent) character.damageVariation += value as Float else character.damageVariation *= value as Float
            StatsEnum.DEFENCE -> if (!percent) character.defence += value as Float else character.defence *= value as Float
            StatsEnum.EVASION -> if (!percent) character.evasion += value as Float else character.evasion *= value as Float
            StatsEnum.ACCURACY -> if (!percent) character.accuracy += value as Float else character.accuracy *= value as Float
            StatsEnum.CRITICALCHANCE -> if (!percent) character.criticalChance += value as Float else character.criticalChance *= value as Float
            StatsEnum.CRITICALDAMAGE -> if (!percent) character.criticalDamage += value as Float else character.criticalDamage *= value as Float
            StatsEnum.ATTACKSPEED -> if (!percent) character.attackSpeed += value as Double else character.attackSpeed *= value as Double
            StatsEnum.MOVEMENTSPEED -> if (!percent) character.movementSpeed += value as Double else character.movementSpeed *= value as Double
            StatsEnum.RANGE -> if (!percent) character.range += value as Int else character.range *= value as Int
            StatsEnum.RANGETYPE -> if (!percent) character.rangeType = value as RangeType else character.rangeType = value as RangeType
            StatsEnum.STEALTH -> if (!percent) character.stealth += value as Float else character.stealth *= value as Float
            StatsEnum.FIREDAMAGE -> if (!percent) character.fireDamage += value as Float else character.fireDamage *= value as Float
            StatsEnum.WATERDAMAGE -> if (!percent) character.waterDamage += value as Float else character.waterDamage *= value as Float
            StatsEnum.EARTHDAMAGE -> if (!percent) character.earthDamage += value as Float else character.earthDamage *= value as Float
            StatsEnum.AIRDAMAGE -> if (!percent) character.airDamage += value as Float else character.airDamage *= value as Float
            StatsEnum.POISONDAMAGE -> if (!percent) character.poisonDamage += value as Float else character.poisonDamage *= value as Float
            StatsEnum.FIREDEFENCE -> if (!percent) character.fireDefence += value as Float else character.fireDefence *= value as Float
            StatsEnum.WATERDEFENCE -> if (!percent) character.waterDefence += value as Float else character.waterDefence *= value as Float
            StatsEnum.EARTHDEFENCE -> if (!percent) character.earthDefence += value as Float else character.earthDefence *= value as Float
            StatsEnum.AIRDEFENCE -> if (!percent) character.airDefence += value as Float else character.airDefence *= value as Float
            StatsEnum.POISONDEFENCE -> if (!percent) character.poisonDefence += value as Float else character.poisonDefence *= value as Float
        }
    }

    override fun stop() {
        checkData()

        val character = data
        when (stat) {
            StatsEnum.HPMAX -> if (!percent) character.hpMax -= value as Float else character.hpMax /= value as Float
            StatsEnum.MPMAX -> if (!percent) character.mpMax -= value as Float else character.mpMax /= value as Float
            StatsEnum.STRENGTH -> if (!percent) character.strength -= value as Float else character.strength /= value as Float
            StatsEnum.DEXTERITY -> if (!percent) character.dexterity -= value as Float else character.dexterity /= value as Float
            StatsEnum.INTELLIGENCE -> if (!percent) character.intelligence -= value as Float else character.intelligence /= value as Float
            StatsEnum.LUCK -> if (!percent) character.luck -= value as Float else character.luck /= value as Float
            StatsEnum.DAMAGE -> if (!percent) character.damage -= value as Float else character.damage /= value as Float
            StatsEnum.DAMAGEVARIATION -> if (!percent) character.damageVariation -= value as Float else character.damageVariation /= value as Float
            StatsEnum.DEFENCE -> if (!percent) character.defence -= value as Float else character.defence /= value as Float
            StatsEnum.EVASION -> if (!percent) character.evasion -= value as Float else character.evasion /= value as Float
            StatsEnum.ACCURACY -> if (!percent) character.accuracy -= value as Float else character.accuracy /= value as Float
            StatsEnum.CRITICALCHANCE -> if (!percent) character.criticalChance -= value as Float else character.criticalChance /= value as Float
            StatsEnum.CRITICALDAMAGE -> if (!percent) character.criticalDamage -= value as Float else character.criticalDamage /= value as Float
            StatsEnum.ATTACKSPEED -> if (!percent) character.attackSpeed -= value as Double else character.attackSpeed /= value as Double
            StatsEnum.MOVEMENTSPEED -> if (!percent) character.movementSpeed -= value as Double else character.movementSpeed /= value as Double
            StatsEnum.RANGE -> if (!percent) character.range -= value as Int else character.range /= value as Int
            StatsEnum.RANGETYPE -> if (!percent) character.rangeType = value as RangeType else character.rangeType = value as RangeType
            StatsEnum.STEALTH -> if (!percent) character.stealth -= value as Float else character.stealth /= value as Float
            StatsEnum.FIREDAMAGE -> if (!percent) character.fireDamage -= value as Float else character.fireDamage /= value as Float
            StatsEnum.WATERDAMAGE -> if (!percent) character.waterDamage -= value as Float else character.waterDamage /= value as Float
            StatsEnum.EARTHDAMAGE -> if (!percent) character.earthDamage -= value as Float else character.earthDamage /= value as Float
            StatsEnum.AIRDAMAGE -> if (!percent) character.airDamage -= value as Float else character.airDamage /= value as Float
            StatsEnum.POISONDAMAGE -> if (!percent) character.poisonDamage -= value as Float else character.poisonDamage /= value as Float
            StatsEnum.FIREDEFENCE -> if (!percent) character.fireDefence -= value as Float else character.fireDefence /= value as Float
            StatsEnum.WATERDEFENCE -> if (!percent) character.waterDefence -= value as Float else character.waterDefence /= value as Float
            StatsEnum.EARTHDEFENCE -> if (!percent) character.earthDefence -= value as Float else character.earthDefence /= value as Float
            StatsEnum.AIRDEFENCE -> if (!percent) character.airDefence -= value as Float else character.airDefence /= value as Float
            StatsEnum.POISONDEFENCE -> if (!percent) character.poisonDefence -= value as Float else character.poisonDefence /= value as Float
        }
    }
}