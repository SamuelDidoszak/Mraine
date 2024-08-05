package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils

class ActiveShield(caster: Entity, val baseDefence: Float = 2f, val defenceIncrement: Float = 0.35f): Skill.ActivatedPassiveSkill(
    "Active shield",
    "Actively increase defence",
    SkillType.DEFENCE,
    "skillActiveShield",
    caster,
    Requirements.Stats(strength = 1f)
) {

    private var defenceAdded = 0f

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Defence") + "Additional defence %" to (defenceIncrement * 100).toInt()
    )

    override fun useStart() {
        defenceAdded = caster.get(DefensiveStats::class)!!.defence * defenceIncrement + baseDefence
        caster.get(DefensiveStats::class)!!.defence += defenceAdded
    }

    override fun useStop() {
        caster.get(DefensiveStats::class)!!.defence -= defenceAdded
        defenceAdded = 0f
    }
}