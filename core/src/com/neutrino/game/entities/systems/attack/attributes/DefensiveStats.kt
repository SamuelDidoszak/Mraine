package com.neutrino.game.entities.systems.attack.attributes

import com.badlogic.gdx.graphics.Color
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.characters.attributes.util.CharacterTag.IncreaseStealthDamage
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.EnemyAi
import com.neutrino.game.entities.systems.attack.callables.*
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.entities.systems.util.visuals.Visuals
import com.neutrino.game.entities.util.AttributeOperations
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.compareDelta
import com.neutrino.game.util.roundOneDecimal
import kotlin.random.Random

class DefensiveStats(
    var hpMax: Float = 1f,
    hp: Float = hpMax,
    var mpMax: Float = 0f,
    mp: Float = mpMax,
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
): Attribute(), AttributeOperations<DefensiveStats> {

    var hp = hp
        set(value) {
            val difference = value - hp
            field = value
            entity.call(StatsChangedCallable::class, StatsEnum.HP, difference)
        }
    var mp = mp
        set(value) {
            val difference = value - mp
            field = value
            entity.call(StatsChangedCallable::class, StatsEnum.MP, difference)
        }

    fun getDamage(attacker: OffensiveStats) {
//        if (!this.isAlive())
//            return
        attacker.entity.call(AttackedBeforeCallable::class, entity)

        val evaded = Random.nextFloat() * (1 - attacker.accuracy + evasion)
        if (evaded != 0f && evaded in 0f .. evasion) {
            println("Evaded the attack")
            entity.call(GotAttackedAfterCallable::class, attacker.entity, null)
            attacker.entity.call(AttackedAfterCallable::class, entity, null)
            return
        }

        var damage = 0f
        val attackerDmg = attacker.getDamage()
        val physicalDamage = attackerDmg * attackerDmg / (attackerDmg + defence)
        val fireDamage = attacker.getFireDamage() * (1 - fireDefence)
        val waterDamage = attacker.getWaterDamage() * (1 - waterDefence)
        val airDamage = attacker.getAirDamage() * (1 - airDefence)
        var poisonDamage = attacker.getPoisonDamage() * (1 - poisonDefence)
        poisonDamage = if (hp - poisonDamage <= 1) hp - 1f else poisonDamage

        damage += physicalDamage
        damage += fireDamage
        damage += waterDamage
        damage += airDamage
        damage += poisonDamage

        if (entity.get(EnemyAi::class)?.sensedEnemyArray?.contains(attacker.entity) == false) {
            println("Stealth hit!")
            val multiplier = attacker.entity.get(CharacterTags::class)?.getTag(IncreaseStealthDamage::class)?.incrementPercent ?: 1f
            damage *= attacker.criticalDamage * multiplier
        }
        else if (Random.nextFloat() < attacker.criticalChance) {
            println("Critical hit!")
            damage *= attacker.criticalDamage
        }

        // get damage color from interpolation
        var damageColor: Color = Color(0f, 0f, 0f, 1f)
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 0f, 0f, 1f), (physicalDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 128f, 0f, 1f), (fireDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 0f, 255f, 1f), (waterDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 255f, 255f, 1f), (airDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(128f, 255f, 0f, 1f), (poisonDamage / damage).toInt())

        damageColor = ColorUtils.applySaturation(damageColor, 0.8f)

        Visuals.showDamage(entity, damageColor, damage)

        hp -= damage
        hp = hp.roundOneDecimal()
        if (hp <= 0) {
            hp = 0f
            entity.call(EntityDiedCallable::class, entity)
            if (entity is Character)
                GlobalData.notifyObservers(GlobalDataType.CHARACTERDIED, this.entity)
        } else entity.call(GotAttackedAfterCallable::class, attacker.entity, damage)
        attacker.entity.call(AttackedAfterCallable::class, entity, damage)
    }

    fun isAlive(): Boolean {
        return hp.compareDelta(0f) == 1
    }

    override fun plusEquals(other: DefensiveStats) {
        hpMax += other.hpMax
        hp += other.hp
        mpMax += other.mpMax
        mp = mpMax + other.mpMax
        defence += other.defence
        evasion += other.evasion
        movementSpeed += other.movementSpeed
        stealth += other.stealth
        fireDefence += other.fireDefence
        waterDefence += other.waterDefence
        airDefence += other.airDefence
        poisonDefence += other.poisonDefence
    }
    override fun minusEquals(other: DefensiveStats) {
        hpMax -= other.hpMax
        hp -= other.hp
        mpMax -= other.mpMax
        mp -= other.mp
        defence -= other.defence
        evasion -= other.evasion
        movementSpeed -= other.movementSpeed
        stealth -= other.stealth
        fireDefence -= other.fireDefence
        waterDefence -= other.waterDefence
        airDefence -= other.airDefence
        poisonDefence -= other.poisonDefence
    }
    override fun clone(): DefensiveStats {
        return DefensiveStats(
            hpMax, hp, mpMax, mp, defence, evasion,
            movementSpeed, stealth, fireDefence,
            waterDefence, airDefence, poisonDefence
        )
    }
    override fun isEqual(other: DefensiveStats): Boolean {
        return hpMax == other.hpMax &&
                hp == other.hp &&
                mpMax == other.mpMax &&
                mpMax == other.mpMax &&
                defence == other.defence &&
                evasion == other.evasion &&
                movementSpeed == other.movementSpeed &&
                stealth == other.stealth &&
                fireDefence == other.fireDefence &&
                waterDefence == other.waterDefence &&
                airDefence == other.airDefence &&
                poisonDefence == other.poisonDefence
    }
}