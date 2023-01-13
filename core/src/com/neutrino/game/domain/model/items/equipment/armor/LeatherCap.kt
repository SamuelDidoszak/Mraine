package com.neutrino.game.domain.model.items.equipment.armor

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.ItemType
import kotlin.math.roundToInt

class LeatherCap: EquipmentItem(), ItemType.EQUIPMENT.HEAD {
    override val name: String = "Leather cap"
    override val description: String = "It may be just a leather cap, but it's better than no cap, no cap"

    override val textureNames: List<String> = listOf("leatherCap")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 5f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}