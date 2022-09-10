package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants.DefaultTextures
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.equipment.EqElement
import com.neutrino.game.domain.model.items.equipment.Equipment
import com.neutrino.game.domain.model.turn.Turn

object Player : Character(0, 0, 0.0), Animated {
    override var hp: Float = 10f
    override var currentHp: Float = hp
    override var mp: Float = 10f
    override var currentMp: Float = mp
    override var attack: Float = 0f
    override var strength: Float = 7f
    override var defence: Float = 2f
    override var agility: Float = 2f
    override var evasiveness: Float = 2f
    override var accuracy: Float = 2f
    override var criticalChance: Float = 0.3f
    override var luck: Float = 2f
    override var attackSpeed: Double = 1.0
    override var movementSpeed: Double = 0.25
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE
    override var experience: Float = 0f

    // environmental stats
    override var fireDamage: Float = 0f
    override var waterDamage: Float = 0f
    override var earthDamage: Float = 0f
    override var airDamage: Float = 0f
    override var poisonDamage: Float = 0f
    override var fireDefence: Float = 0f
    override var waterDefence: Float = 0f
    override var earthDefence: Float = 0f
    override var airDefence: Float = 0f
    override var poisonDefence: Float = 0f

    init {
        setName("Player")
        attack = setAttack()
    }

    // Equipment
    /** Player equipment */
    val equipment: Equipment = Equipment()
    val equipped: Equipment = Equipment()

    override val description: String
        get() = TODO("Not yet implemented")

    override var textureSrc: String = "characters/player.png"
    override val textureNames: List<String> = listOf(
        "buddy#1", "buddy#2", "buddy#3", "buddy#4", "buddy#5"
    )
    override var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    override var texture: TextureAtlas.AtlasRegion = if(textureList.isNotEmpty()) textureList[0] else TextureAtlas.AtlasRegion(DefaultTextures[6][5])

    override val defaultAnimationName: String = "buddy"
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override val textureHaver: TextureHaver = this

    override var animation: Animation<TextureRegion>? = null

    fun addToEquipment(item: Item) {
        // add to stack
        if (item.stackable) {
            val stackableItem = equipment.itemList.find { it.item.name == item.name }
            if (stackableItem != null)
                stackableItem.item.amount = stackableItem.item.amount!!.plus(item.amount!!)
            else
                equipment.itemList.add(EqElement(item, Turn.turn))
        } else
            equipment.itemList.add(EqElement(item, Turn.turn))
    }
}