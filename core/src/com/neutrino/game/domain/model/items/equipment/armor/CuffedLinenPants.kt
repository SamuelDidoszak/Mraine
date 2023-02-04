package com.neutrino.game.domain.model.items.equipment.armor

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.utility.serialization.AtlasRegion
import com.neutrino.game.utility.serialization.EquipmentItemSerializer
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable(with = EquipmentItemSerializer::class)
class CuffedLinenPants: EquipmentItem(), ItemType.EQUIPMENT.LEGS {
    override val name: String = "Cuffed linen pants"
    override val description: String = "Cuffed linen pants"

    override val textureNames: List<String> = listOf("cuffedLinenPants")
    override var texture: AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 10.0f)),
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}