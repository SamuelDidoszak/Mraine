package com.neutrino.game.domain.model.items.equipment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.Data
import com.neutrino.game.domain.model.event.Requirement
import com.neutrino.game.domain.model.event.types.EventModifyStat
import com.neutrino.game.domain.model.event.wrappers.EqItemStat
import com.neutrino.game.domain.model.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import kotlin.math.roundToInt

class Knife: EquipmentItem(), ItemType.EQUIPMENT.RHAND {
    override val handedItemType: HandedItemType = HandedItemType.DAGGER
    override val name: String = "Knife"
    override val description: String = "Stabby stab stab"

    override val textureNames: List<String> = listOf("knife")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 15

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        EqItemStat(EventModifyStat(StatsEnum.DAMAGE, 1f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )
    init {
        requirements.add { (requirements.data["character"]?.data as Character).strength.compareDelta(0f) >= 0  }
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}