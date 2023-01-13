package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.utility.HasProjectile
import com.neutrino.game.domain.model.items.utility.Projectile
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.ProjectileAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import kotlin.math.roundToInt

class CurvedBow: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HasProjectile {
    override val handedItemType: HandedItemType = HandedItemType.BOW
    override val name: String = "Curved bow"
    override val description: String = "Flexible and strong"

    override val textureNames: List<String> = listOf("curvedBow")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 45

    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.WOODENARROW

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 3f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGEVARIATION, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, 6))
    )

    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    init {
        requirements.add { requirements.get("character", Character::class)!!.dexterity.compareDelta(2f) >= 0  }

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}