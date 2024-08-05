package com.neutrino.game.entities.systems.skills.passive

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.GotAttackedAfterCallable
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.entities.systems.util.visuals.Visuals
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.position

class Parry(caster: Entity, val parryChance: Float = 0.35f, val repostChance: Float = 0.3f): Skill.PassiveSkill(
    "Parry",
    "Chance to parry an attack with a chance of a repost",
    SkillType.DEXTERITY,
    "skillParry",
    caster,
    Requirements.Stats(dexterity = 4f)
) {

    private var previousEvasion = 0f

    private val parryCallable = object : GotAttackedAfterCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            if (data[1] != null)
                return
            Visuals.showText(entity, ColorUtils.getStatColorTextra("Dexterity") + "Repost")
            entity.get(OffensiveStats::class)!!.attack((data[0] as Entity).position)
        }
    }

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Dexterity") + "Chance to parry" to (parryChance * 100).toInt(),
        ColorUtils.getStatColorTextra("Dexterity") + "Chance to repost" to (repostChance * 100).toInt()
    )

    override fun useStart() {
        val defensiveStats = caster.get(DefensiveStats::class)!!
        previousEvasion = defensiveStats.evasion
        defensiveStats.evasion = parryChance
        caster.attach(parryCallable)
    }

    override fun useStop() {
        caster.get(DefensiveStats::class)!!.evasion = previousEvasion
        previousEvasion = 0f
        caster.detach(parryCallable)
    }
}