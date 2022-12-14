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

class BigBow: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HasProjectile {
    override val handedItemType: HandedItemType = HandedItemType.BOW
    override val name: String = "Big bow"
    override val description: String = "Almost the same size as you"

    override val textureNames: List<String> = listOf("bigBow")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 65

    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.WOODENARROW

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 5f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGEVARIATION, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, 7))
    )
    init {
        requirements.add { requirements.get("character", Character::class)!!.dexterity.compareDelta(4f) >= 0  }

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}