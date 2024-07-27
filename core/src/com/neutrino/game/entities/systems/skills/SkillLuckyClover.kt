package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillLuckyClover(caster: Entity): Skill.ActiveSkill(
    "Lucky clover",
    "Makes you the luckiest fighter with his luckiest blows",
    SkillType.ROGUE,
    "skillLuckyClover",
    null,
    40.0,
    caster,
    Requirements.Stats(luck = 5f)
) {

    private val criticalChance = 0.2f
    private val criticalDamage = 0.5f
    private val sustainTime = 7.0

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("criticalChance") + "Critical chance %" to (criticalChance * 100).toInt(),
        ColorUtils.getStatColorTextra("criticalDamage") + "Critical damage %" to (criticalDamage * 100).toInt(),
        ColorUtils.getStatColorTextra("attackSpeed") + "Lasts for $sustainTime turns" to "",
        ColorUtils.getStatColorTextra("cooldown") + "Cooldown" to cooldown
    )

    override fun use() {
        Events.addEvent(caster, TimedEvent(
            CharacterEvents.AddStats(
                OffensiveStats(criticalChance = criticalChance, criticalDamage = criticalDamage), null, "Lucky"),
            sustainTime, 1)
        )
    }
}



















