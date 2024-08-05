package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils

class SilentWalk(caster: Entity, val stealth: Float = 0.25f): Skill.PassiveSkill(
    "Silent walk",
    "You walk silently",
    SkillType.DEFENCE,
    "skillSilentWalk",
    caster,
    Requirements.Stats(dexterity = 4f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Stealth") + "Stealth" to (stealth * 100).toInt()
    )

    override fun useStart() {
        caster.get(DefensiveStats::class)!!.stealth += stealth
    }

    override fun useStop() {
        caster.get(DefensiveStats::class)!!.stealth -= stealth
    }
}