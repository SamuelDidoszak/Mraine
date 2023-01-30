package com.neutrino.game.domain.model.systems.skills.passive

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasEquipment
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import kotlin.reflect.KClass

class IncreaseMeleeDamage(override val character: Character): Skill.PassiveSkill {
    override val skillType: SkillType = SkillType.STRENGTH
    override val name: String = "Increase melee damage"
    override val description: String = "Increase melee damage"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Strength", 1f) { Player.strength })
        { (character !is Player) || (character is Player && character.strength >= 1) }

    private val increment: Float = 1.1f

    override val printableData: List<Pair<String, () -> Any>> = listOf(
        Pair("Additional damage %") { (increment * 100) - 100}
    )
    override val textureName: String = "skillTeleportBackstab"
    override val manaCost: Float? = null

    override val playerRequirements: List<Pair<KClass<Skill.PassiveSkill>, Boolean>> = listOf()

    override fun useStart() {
        val hasEquipment = character is HasEquipment
        var previousItem: EquipmentItem? = null
        if (hasEquipment) {
            previousItem = (character as HasEquipment).equipment.getEquipped(EquipmentType.RHAND)
            if (previousItem != null)
                character.equipment.unequipItem(previousItem, false)
        }

        character.addTag(CharacterTag.IncreaseMeleeDamage(increment))

        if (hasEquipment && previousItem != null)
            (character as HasEquipment).equipment.setItem(previousItem)
    }

    override fun useStop() {
        val hasEquipment = character is HasEquipment
        var previousItem: EquipmentItem? = null
        if (hasEquipment) {
            previousItem = (character as HasEquipment).equipment.getEquipped(EquipmentType.RHAND)
            if (previousItem != null)
                character.equipment.unequipItem(previousItem, false)
        }

        character.removeTag(CharacterTag.IncreaseMeleeDamage::class)

        if (hasEquipment && previousItem != null)
            (character as HasEquipment).equipment.setItem(previousItem)
    }
}