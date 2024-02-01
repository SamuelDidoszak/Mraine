package com.neutrino.game.entities.characters.attributes

import com.badlogic.gdx.graphics.Color
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.domain.model.characters.Player.shaders
import com.neutrino.game.domain.model.systems.CharacterTag.IncreaseStealthDamage
import com.neutrino.game.domain.model.systems.CharacterTag.Lifesteal
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.attributes.EnemyAi
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.compareDelta
import kotlin.random.Random

class DefensiveStats(
    var hpMax: Float = 1f,
    var hp: Float = hpMax,
    var mpMax: Float = 0f,
    var mp: Float = 0f,
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
): Attribute() {

    fun getDamage(attacker: OffensiveStats) {
//        if (!this.isAlive())
//            return

        val evaded = Random.nextFloat() * (1 - attacker.accuracy + evasion)
        if (evaded != 0f && evaded in 0f .. evasion) {
            println("Evaded the attack")
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

        if (entity.get(EnemyAi::class)?.sensedEnemyArray?.contains(attacker.entity) == true) {
            println("Stealth hit!")
            val multiplier = attacker.entity.get(CharacterTags::class)?.getTag(IncreaseStealthDamage::class)?.incrementPercent ?: 1f
            println("Multiplier: $multiplier")
            damage *= attacker.criticalDamage * multiplier
        }
        else if (Random.nextFloat() < attacker.criticalChance) {
            println("Critical hit!")
            damage *= attacker.criticalDamage
        }

        if (attacker.entity.get(CharacterTags::class)?.getTag(Lifesteal::class) != null) {
            // TODO ECS Events
//            val heal = CharacterEvent(attacker.entity,
//                TimedEvent(1.0,
//                    EventHeal(damage * attacker.entity.get(CharacterTags::class)?.getTag(Lifesteal::class)!!.power)
//                ), Turn.turn)
//            EventDispatcher.dispatchEvent(heal)
        }

        // get damage color from interpolation
        var damageColor: Color = Color(0f, 0f, 0f, 1f)
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 0f, 0f, 1f), (physicalDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(255f, 128f, 0f, 1f), (fireDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 0f, 255f, 1f), (waterDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(0f, 255f, 255f, 1f), (airDamage / damage).toInt())
        damageColor = ColorUtils.colorInterpolation(damageColor, Color(128f, 255f, 0f, 1f), (poisonDamage / damage).toInt())

        damageColor = ColorUtils.applySaturation(damageColor, 0.8f)

        // TODO ECS Actors
//        ActorVisuals.showDamage(this, damageColor, damage)

        entity.get(EnemyAi::class)?.gotAttackedBy = attacker.entity

        this.hp -= damage
        if (hp <= 0) {
            shaders.clear()
            // TODO ECS Actors
//            this.addAction(
//                Actions.sequence(
//                Actions.fadeOut(1.25f),
//                Actions.removeActor()
//            ))
            hp = 0f
            GlobalData.notifyObservers(GlobalDataType.CHARACTERDIED, this)
        }
        // TODO ECS Actors
//        this.findActor<HpBar>("hpBar")?.update(hp)
    }

    fun isAlive(): Boolean {
        return hp.compareDelta(0f) == 1
    }
}