package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillGutAttack(caster: Entity): Skill.ActiveSkillEntity(
    "Gut attack",
    "Stronger attack which aims for enemy weaker parts",
    SkillType.STRENGTH,
    "skillGutAttack",
    null,
    20.0,
    caster,
    1,
    RangeType.SQUARE,
    Requirements.WeaponType("melee")
) {

    val damage: Float = 7f

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("damage") + "Damage" to damage,
        ColorUtils.getStatColorTextra("Cooldown") + "Cooldown" to cooldown
    )

    override fun use(target: Entity) {
        val offensiveStats = caster.get(OffensiveStats::class)!!.clone()
        offensiveStats.entity = caster
        offensiveStats.damageMin += damage
        offensiveStats.damageMax += damage

        playAnimation("attack3")
        target.get(DefensiveStats::class)!!.getDamage(offensiveStats)
        causeCooldown()
    }
}