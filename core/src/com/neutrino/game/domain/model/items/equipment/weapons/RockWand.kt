package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.neutrino.game.compareDelta
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

class RockWand: EquipmentItemRanged(), ItemType.EQUIPMENT.RHAND, HeaderSerializable {
    @Transient
    override val handedItemType: HandedItemType = HandedItemType.WAND
    @Transient
    override val name: String = "Rock wand"
    @Transient
    override val description: String = "Do You want to go back to your ancestral roots and stone people, but are too lazy for it? " +
        "With the new MagicOoga technology You can make rocks materialize out of thin air!"

    @Transient
    override val textureNames: List<String> = listOf("rockWand")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    @Transient
    override var goldValueOg: Int = 75

    @Transient
    override var range: Int = 6
    @Transient
    override var rangeType: RangeType = RangeType.SQUARE
    @Transient
    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.ROCK

    @Transient
    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.EARTH_DAMAGE, 5f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE_VARIATION, 3.5f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, range))
    )

    @Transient
    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    override fun readAfter(kryo: Kryo?, input: Input?) {
        attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))
    }

    init {
        requirements
            .add(RequirementPrintable.PrintableReq("Intelligence", 3f)
            { requirements.get("character", Character::class)!!.intelligence })
            { requirements.get("character", Character::class)!!.intelligence.compareDelta(3f) >= 0 }

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}