package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillBleed(caster: Entity): Skill.ActiveSkillCharacter(
    "Bleed",
    "Physical attack that induces bleeding.",
    SkillType.ROGUE,
    "skillBleed",
    null,
    20.0,
    caster,
    1,
    RangeType.SQUARE,
    Requirements.Stats(dexterity = 2f),
    Requirements.Custom({entity: Entity ->
        entity.get(Equipment::class)?.getWeapon()?.get(EquipmentItem::class)?.isMelee() == true
    }, "Melee weapon required", "")
) {

    private val bleedDamage = 2f
    private val bleedingLength = 5

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Bleed damage" to bleedDamage,
        ColorUtils.getStatColorTextra("Damage") + "Bleeding length" to bleedingLength,
        ColorUtils.getStatColorTextra("Cooldown") + "Cooldown" to cooldown
    )

    override fun use(target: Character) {
        target.get(DefensiveStats::class)!!.getDamage(caster.get(OffensiveStats::class)!!)
        Events.addEvent(target, TimedEvent(CharacterEvents.Bleed(bleedDamage), 1.0, bleedingLength))
        causeCooldown()
    }
}