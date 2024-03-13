package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.add

class EquipmentInitializer(
    val textureName: String,
    val goldValue: Int,
    val eqType: EquipmentType,
    val handheldType: HandheldEquipmentType? = null,
    val tier: Int = 3

): Attribute() {

    override fun onEntityAttached() {
        entity.addAttribute(Texture { position, random, textures -> run {
            textures add Textures.get(textureName)
        }})
        entity.addAttribute(GoldValue(goldValue))
        entity.addAttribute(Amount(maxStack = 1))
        if (handheldType != null)
            entity.addAttribute(HandheldEquipment(handheldType, eqType))
        else
            entity.addAttribute(EquipmentItem(eqType))
        entity.addAttribute(ItemTier(tier))
        entity.addAttribute(Interaction(arrayListOf(InteractionType.ITEM())))
        entity.removeAttribute(EquipmentInitializer::class)
    }
}












