package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItemRanged
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.utility.Projectile
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.ProjectileAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.utility.serialization.HeaderSerializable

import kotlin.math.roundToInt

class ExtinguishedFireWand: EquipmentItemRanged(), ItemType.EQUIPMENT.RHAND, HeaderSerializable {
    @Transient
    override val handedItemType: HandedItemType = HandedItemType.WAND
    @Transient
    override val name: String = "Extinguished fire wand"
    @Transient
    override val description: String = "A piece of red rock on a stick"

    @Transient
    override val textureNames: List<String> = listOf("extinguishedFireWand")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    @Transient
    override var goldValueOg: Int = 15

    @Transient
    override var range: Int = 5
    @Transient
    override var rangeType: RangeType = RangeType.SQUARE
    @Transient
    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.FIREPROJECTILE

    @Transient
    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.FIRE_DAMAGE, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE_VARIATION, 1f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, range))
    )

    @Transient
    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    override fun readAfter(kryo: Kryo?, input: Input?) {
        attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))
    }

    init {
        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}