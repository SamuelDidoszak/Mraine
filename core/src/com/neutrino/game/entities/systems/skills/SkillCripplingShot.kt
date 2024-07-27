package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.AroundAttack
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.AttackedAfterCallable
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillCripplingShot(caster: Entity): Skill.ActiveSkillEntity(
    "Crippling shot",
    "A nordic technique for precisely aiming shots at enemy kneecaps",
    SkillType.RANGED,
    "skillCripplingShot",
    null,
    30.0,
    caster,
    6,
    RangeType.CIRCLE,
    Requirements.Stats(dexterity = 5f),
    Requirements.WeaponType("Ranged")
) {

    private val damage = 2f
    private val crippleTime = 2.0

    private val crippleCallable = object : AttackedAfterCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            if ((data[0] as Entity).get(DefensiveStats::class)?.isAlive() == true)
                // power is temporary
                Events.addEvent(data[0] as Entity, TimedEvent(CharacterEvents.SlowDown(3.0), crippleTime, 1))
        }
    }

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("damage") + "Damage" to damage,
        ColorUtils.getStatColorTextra("movementSpeed") + "Cripple time" to crippleTime,
        ColorUtils.getStatColorTextra("range") + "Range" to range,
        ColorUtils.getStatColorTextra("cooldown") + "Cooldown" to cooldown
    )

    override fun use(target: Entity) {
        caster.attach(crippleCallable)

        caster.get(OffensiveStats::class)!!.clone().also {
            it.damageMin += damage
            it.damageMax += damage
            it.range = range
            it.rangeType = rangeType
            it.entity = caster
        }.attack(target.get(Position::class)!!)

        caster.get(AroundAttack::class)?.minusEquals(AroundAttack())
        caster.detach(crippleCallable)
        causeCooldown()
    }
}