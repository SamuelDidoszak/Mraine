package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillShieldBash(caster: Entity): Skill.ActiveSkillEntity(
    "Shield bash",
    "Bash your shield into the enemy. Damage is based on defence",
    SkillType.DEFENCE,
    "skillShieldBash",
    null,
    20.0,
    caster,
    1,
    RangeType.SQUARE,
    Requirements.Stats(strength = 3f)
) {
    private val damage: Float = 2f
    private val defPlusDmg: Float
        get() = caster.get(DefensiveStats::class)!!.defence * 1.2f + damage

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Damage" to defPlusDmg,
        ColorUtils.getStatColorTextra("Range") + "Range" to range
    )

    override fun use(target: Entity) {
        target.get(DefensiveStats::class)!!.getDamage(
            OffensiveStats(damageMin = defPlusDmg, damageMax = defPlusDmg).also { it.entity = target }
        )
        causeCooldown()
    }
}