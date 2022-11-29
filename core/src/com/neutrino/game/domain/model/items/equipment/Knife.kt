package com.neutrino.game.domain.model.items.equipment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.Event
import com.neutrino.game.domain.model.event.types.EventModifyStat
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

    override val modifierList: ArrayList<Event<*>> = arrayListOf(
        EventModifyStat(StatsEnum.DAMAGE, 10f),
        // TODO DYNAMIC PLAYER
        // example
//        Event.HEAL(Player, false, 0f, Turn.turn, 1.0, Int.MAX_VALUE)
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}