package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillMeteorite(caster: Entity): Skill.ActiveSkillArea(
    "Meteorite",
    "Summon a small celestial body to aid you in demolishing your enemies",
    SkillType.INTELLIGENCE,
    "book",
    10f,
    20.0,
    caster,
    10,
    RangeType.CIRCLE,
    Requirements.Stats(intelligence = 10f)
) {

    private val fireDamage: Float = 10f
    private val burnDamage: Float = 5f
    private val burnLength: Double = 10.0
    override val area: HasRange = object: HasRange {
        override var range: Int = 5
        override var rangeType: RangeType = RangeType.CIRCLE
    }

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("FireDamage") + "Fire damage" to fireDamage,
        ColorUtils.getStatColorTextra("FireDamage") + "Burn damage" to burnDamage,
        ColorUtils.getStatColorTextra("FireDamage") + "Burn length" to burnLength,
        ColorUtils.getStatColorTextra("Range") + "Range" to range
    )

    override fun use(target: Position) {
        val offensiveStats = OffensiveStats(fireDamageMin = fireDamage, fireDamageMax = fireDamage).also { it.entity = caster }
        for (position in area.getTilesInRange(target)) {
            OffensiveStats.getAllAttackables(position)?.forEach {
                it.get(DefensiveStats::class)!!.getDamage(offensiveStats)
                if (it.get(DefensiveStats::class)?.isAlive() == true)
                    Events.addEvent(it, TimedEvent(CharacterEvents.Burn(burnDamage), 1.0, burnLength.toInt()))
            }
        }
        causeCooldown()
    }
}