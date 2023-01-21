package com.neutrino.game.domain.model.systems.skills.passive

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasEquipment
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.skills.Skill

class IncreaseTwohandedDamage(override val character: Character, val increment: Float = 1.1f): Skill.PassiveSkill {
    override val name: String = "Increase onehanded damage"
    override val description: String = "Increase onehanded damage"
    override val requirement: RequirementPrintable = RequirementPrintable().add("Strength: 1") {
        (character !is Player) || (character is Player && character.strength >= 1)
    }

//    val increment: Float = 1.1f

    override val printableData: List<Pair<String, Any>> = listOf(
        Pair("Additional damage %", (increment * 100) - 100)
    )
    override val textureName: String = "skillTeleportBackstab"
    override val manaCost: Float? = null

    override fun useStart() {
        val hasEquipment = character is HasEquipment
        var previousItem: EquipmentItem? = null
        if (hasEquipment) {
            previousItem = (character as HasEquipment).equipment.getEquipped(EquipmentType.RHAND)
            if (previousItem != null)
                character.equipment.unsetItem(previousItem, false)
        }

        character.addTag(CharacterTag.IncreaseTwohandedDamage(increment))

        if (hasEquipment && previousItem != null)
            (character as HasEquipment).equipment.setItem(previousItem)
    }

    override fun useStop() {
        val hasEquipment = character is HasEquipment
        var previousItem: EquipmentItem? = null
        if (hasEquipment) {
            previousItem = (character as HasEquipment).equipment.getEquipped(EquipmentType.RHAND)
            if (previousItem != null)
                character.equipment.unsetItem(previousItem, false)
        }

        character.removeTag(CharacterTag.IncreaseTwohandedDamage::class)

        if (hasEquipment && previousItem != null)
            (character as HasEquipment).equipment.setItem(previousItem)
    }
}