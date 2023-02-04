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
import kotlin.math.roundToInt

class ReinforcedLeatherBoots: EquipmentItem(), ItemType.EQUIPMENT.FEET {
    override val name: String = "Reinforced leather boots"
    override val description: String = "Sturdy and comfy"

    override val textureNames: List<String> = listOf("reinforcedLeatherBoots")
    override var texture: AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 15.0f)),
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}