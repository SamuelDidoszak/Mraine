package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.AroundAttack
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.AttackedAfterCallable
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.requirements.Requirements

class SkillCripplingSpin(caster: Entity): Skill.ActiveSkill(
    "Crippling spin",
    "Spinning attack which slows down nearby enemies",
    SkillType.STRENGTH,
    "book",
    null,
    20.0,
    caster,
    Requirements.Stats(strength = 2f)
), HasRange {

    override var range: Int = 2
    override var rangeType: RangeType = RangeType.SQUARE

    private val slowDownStrength = 0.5
    private val slowDownTime = 10.0

    val damage: Float = 5f

    private val slowDownCallable = object : AttackedAfterCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            if ((data[0] as Entity).get(DefensiveStats::class)?.isAlive() == true)
                Events.addEvent(data[0] as Entity, TimedEvent(CharacterEvents.SlowDown(slowDownStrength * -1), slowDownTime, 1))
        }
    }

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        "Damage" to damage,
        "Slowdown" to slowDownStrength,
        "Slowdown time" to slowDownTime,
        "Range" to range
    )

    override fun use() {
        if (caster has AroundAttack::class)
            caster.get(AroundAttack::class)?.plusEquals(AroundAttack())
        else
            caster.addAttribute(AroundAttack())
        caster.attach(slowDownCallable)

        caster.get(OffensiveStats::class)!!.clone().also {
            it.damageMin += damage
            it.damageMax += damage
            it.range = range
            it.rangeType = rangeType
            it.entity = caster
        }.attack(caster.get(Position::class)!!)

        caster.get(AroundAttack::class)?.minusEquals(AroundAttack())
        caster.detach(slowDownCallable)
        causeCooldown()
    }
}