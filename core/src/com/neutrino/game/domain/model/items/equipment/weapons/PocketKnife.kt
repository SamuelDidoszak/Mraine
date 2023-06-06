package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.utility.serialization.HeaderSerializable

import kotlin.math.roundToInt

class PocketKnife: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HeaderSerializable {
    @Transient
    override val handedItemType: HandedItemType = HandedItemType.DAGGER
    @Transient
    override val name: String = "Pocket knife"
    @Transient
    override val description: String = "A small blade"

    @Transient
    override val textureNames: List<String> = listOf("pocketKnife")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    @Transient
    override var goldValueOg: Int = 15

    @Transient
    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 1f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )

    @Transient
    override var attack: Attack = BasicAttack(getDamageTypesFromModifiers(modifierList))

    override fun readAfter(kryo: Kryo?, input: Input?) {
        attack = BasicAttack(getDamageTypesFromModifiers(modifierList))
    }

    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}