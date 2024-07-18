package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils

class SkillTwoshot(caster: Entity): Skill.ActiveSkillEntity(
    "Twoshot",
    "Shoot twice",
    SkillType.DEXTERITY,
    "book",
    null,
    20.0,
    caster,
    0,
    RangeType.SQUARE,
    Requirements.Stats(dexterity = 3f),
    Requirements.Custom( {entity: Entity ->
        entity.get(Equipment::class)?.getEquipped(Equipment.EquipmentType.RHAND)?.get(EquipmentItem::class)?.isRanged() == true
    }, "Weapon type", "ranged")
) {

    override var range: Int = 0
        get() = caster.get(OffensiveStats::class)!!.range
    override var rangeType: RangeType = RangeType.SQUARE
        get() = caster.get(OffensiveStats::class)!!.rangeType

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "DamageMin" to caster.get(OffensiveStats::class)!!.damageMin * 2f,
        ColorUtils.getStatColorTextra("Damage") + "DamageMax" to caster.get(OffensiveStats::class)!!.damageMax * 2f,
        ColorUtils.getStatColorTextra("Cooldown") + "Cooldown" to cooldown
    )

    override fun use(target: Entity) {
        caster.get(OffensiveStats::class)!!.attack(target.get(Position::class)!!)
        caster.get(OffensiveStats::class)!!.attack(target.get(Position::class)!!)
        causeCooldown()
    }
}