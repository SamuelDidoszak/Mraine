package com.neutrino.game.domain.model.items.equipment.misc

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.CharacterParamsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyCharacterParam
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlin.math.roundToInt

class SmallBag: EquipmentItem(), ItemType.EQUIPMENT.BAG {
    override val name: String = "Small bag"
    override val description: String = "A small bag"

    override val textureNames: List<String> = listOf("smallBag")
    override var texture: AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyCharacterParam(CharacterParamsEnum.INVENTORYSIZE, 10))
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}