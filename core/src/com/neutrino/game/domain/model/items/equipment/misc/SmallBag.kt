package com.neutrino.game.domain.model.items.equipment.misc

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.CharacterParamsEnum
import com.neutrino.game.domain.model.event.Data
import com.neutrino.game.domain.model.event.Requirement
import com.neutrino.game.domain.model.event.types.EventModifyCharacterParam
import com.neutrino.game.domain.model.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.ItemType
import kotlin.math.roundToInt

class SmallBag: EquipmentItem(), ItemType.EQUIPMENT.BAG {
    override val name: String = "Small bag"
    override val description: String = "A small bag"

    override val textureNames: List<String> = listOf("smallBag")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyCharacterParam(CharacterParamsEnum.INVENTORYSIZE, 10))
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}