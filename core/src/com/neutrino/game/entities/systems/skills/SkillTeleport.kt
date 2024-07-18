package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.graphics.utility.ColorUtils

class SkillTeleport(caster: Entity): Skill.ActiveSkillPosition(
    "Teleport",
    "Teleports you to a desired place",
    SkillType.INTELLIGENCE,
    "book",
    null,
    2.0,
    caster,
    40,
    RangeType.CIRCLE
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Range") + "Range" to range,
        ColorUtils.getStatColorTextra("RangeType") + "RangeType" to rangeType
    )

    override fun use(target: Position) {
        Events.addEvent(caster, CharacterEvents.Teleport(target).asTimedEvent())
        causeCooldown()
    }
}