package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.Data
import com.neutrino.game.domain.model.event.Requirement
import com.neutrino.game.domain.model.event.types.EventModifyStat
import com.neutrino.game.domain.model.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.utility.HasProjectile
import com.neutrino.game.domain.model.items.utility.Projectile
import kotlin.math.roundToInt

class RockWand: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HasProjectile {
    override val handedItemType: HandedItemType = HandedItemType.WAND
    override val name: String = "Basic poison staff"
//    override val description: String = "Too lazy to throw rocks at people? With the new MagicOoga technology You can make rocks materialize out of thin air!"
    override val description: String = "Do You want to go back to your ancestral roots and stone people, but are too lazy for it? " +
        "With the new MagicOoga technology You can make rocks materialize out of thin air!"

    override val textureNames: List<String> = listOf("rockWand")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 75

    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.ROCK

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.EARTHDAMAGE, 5f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGEVARIATION, 3.5f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, 6))
    )
    init {
        requirements.add { requirements.get("character", Character::class)!!.intelligence.compareDelta(3f) >= 0  }
        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}