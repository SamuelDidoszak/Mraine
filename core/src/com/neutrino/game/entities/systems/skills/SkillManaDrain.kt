package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillManaDrain(caster: Entity): Skill.ActiveSkillCharacter(
    "Mana drain",
    "Drain mana from the enemy",
    SkillType.INTELLIGENCE,
    "book",
    null,
    20.0,
    caster,
    8,
    RangeType.CIRCLE,
    Requirements.Stats(intelligence = 5f)
) {

    var manaDrain = 15f

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> =  listOf(
        ColorUtils.getStatColorTextra("Mp") + "Mana drain" to manaDrain,
        ColorUtils.getStatColorTextra("Range") + "Range" to range
    )

    override fun use(target: Character) {
        val manaDrained =
            if (target.get(DefensiveStats::class)!!.mp > manaDrain)
                manaDrain
            else
                target.get(DefensiveStats::class)!!.mp

        Events.addEvent(target, CharacterEvents.ManaRegen(-1 * manaDrained).asTimedEvent())
        Events.addEvent(caster, CharacterEvents.ManaRegen(manaDrained).asTimedEvent())
        causeCooldown()
    }
}