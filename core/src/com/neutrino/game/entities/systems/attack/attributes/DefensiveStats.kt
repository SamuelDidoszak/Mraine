package com.neutrino.game.entities.systems.attack.attributes

import com.badlogic.gdx.graphics.Color
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.CharacterTags
import com.neutrino.game.entities.characters.attributes.EnemyAi
import com.neutrino.game.entities.characters.attributes.util.CharacterTag.IncreaseStealthDamage
import com.neutrino.game.entities.systems.attack.callables.*
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.entities.systems.util.visuals.Visuals
import com.neutrino.game.entities.util.AttributeOperations
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.compareDelta
import com.neutrino.game.util.roundOneDecimal
import kotlin.random.Random

class DefensiveStats(
    var hpMax: Float = 0f,
    hp: Float = hpMax,
    var mpMax: Float = 0f,
    mp: Float = mpMax,
    var defence: Float = 0f,
    /** Range is 0 - 1 which tells the probability of dodging */
    var evasion: Float = 0f,
    var movementSpeed: Double = 0.0,
    var stealth: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var fireDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var waterDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var airDefence: Float = 0f,
    /** Range is 0 - 2, where 1+ heals instead of damaging */
    var poisonDefence: Float = 0f
): Attribute(), AttributeOperations<DefensiveStats>, PrintableInfo<DefensiveStats> {

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

    override fun onEntityAttached() {
        if (entity !is Character)
            return

        if (hpMax == 0f) {
            hpMax = 1f
            this.hp = hpMax
        }
        if (movementSpeed == 0.0)
            movementSpeed = 1.0
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

    override fun getPrintableInfo(other: DefensiveStats?): List<Pair<String, Any?>> {
        val printableInfo = ArrayList<Pair<String, Any>>()

        if (hpMax != 0f)
            printableInfo.add("Hp max" to "${PrintableInfo.getColor(hpMax.compareDelta(other?.hpMax ?: 0f))}$hpMax")
        if (hp != 0f)
            printableInfo.add("Hp" to "${PrintableInfo.getColor(hp.compareDelta(other?.hp ?: 0f))}$hp")
        if (mpMax != 0f)
            printableInfo.add("Mp max" to "${PrintableInfo.getColor(mpMax.compareDelta(other?.mpMax ?: 0f))}$mpMax")
        if (mp != 0f)
            printableInfo.add("Mp" to "${PrintableInfo.getColor(mp.compareDelta(other?.mpMax ?: 0f))}$mpMax")
        if (defence != 0f)
            printableInfo.add("Defence" to "${PrintableInfo.getColor(defence.compareDelta(other?.defence ?: 0f))}$defence")
        if (evasion != 0f)
            printableInfo.add("Evasion" to "${PrintableInfo.getColor(stealth.compareDelta(other?.evasion ?: 0f))}$evasion")
        if (movementSpeed != 0.0)
            printableInfo.add("Movement speed" to "${PrintableInfo.getColor(movementSpeed.compareDelta(other?.movementSpeed ?: 0.0))}$movementSpeed")
        if (stealth != 0f)
            printableInfo.add("Stealth" to "${PrintableInfo.getColor(stealth.compareDelta(other?.stealth ?: 0f))}$stealth")
        if (fireDefence != 0f)
            printableInfo.add("Fire defence" to "${PrintableInfo.getColor(fireDefence.compareDelta(other?.fireDefence ?: 0f))}$fireDefence")
        if (waterDefence != 0f)
            printableInfo.add("Water defence" to "${PrintableInfo.getColor(waterDefence.compareDelta(other?.waterDefence ?: 0f))}$waterDefence")
        if (airDefence != 0f)
            printableInfo.add("Air defence" to "${PrintableInfo.getColor(airDefence.compareDelta(other?.airDefence ?: 0f))}$airDefence")
        if (poisonDefence != 0f)
            printableInfo.add("Poison defence" to "${PrintableInfo.getColor(poisonDefence.compareDelta(other?.poisonDefence ?: 0f))}$poisonDefence")

        return printableInfo
    }
}