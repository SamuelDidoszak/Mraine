package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils

class Focus(caster: Entity, val accuracy: Float = 0.15f): Skill.PassiveSkill(
    "Focus",
    "You become more target oriented",
    SkillType.RANGED,
    "skillFocus",
    caster,
    Requirements.Stats(dexterity = 4f)
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Accuracy") + "Accuracy" to (accuracy * 100).toInt()
    )

    override fun useStart() {
        caster.get(OffensiveStats::class)!!.accuracy += accuracy
    }

    override fun useStop() {
        caster.get(OffensiveStats::class)!!.accuracy -= accuracy
    }
}