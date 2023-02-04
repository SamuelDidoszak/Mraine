package com.neutrino.game.utility.serialization

import com.neutrino.game.domain.model.items.EquipmentItem
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class EquipmentItemSerializer: KSerializer<EquipmentItem> {
    override val descriptor: SerialDescriptor = Int.serializer().descriptor
    override fun serialize(encoder: Encoder, value: EquipmentItem) {
        val modifiers = value.modifierList
    }

    override fun deserialize(decoder: Decoder): EquipmentItem {
        TODO("Not yet implemented")
    }
}