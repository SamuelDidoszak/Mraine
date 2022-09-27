package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.equipment.utility.EqElement
import com.neutrino.game.domain.model.items.equipment.utility.Equipment
import com.neutrino.game.domain.model.turn.Turn

object Player : Character(0, 0, 0.0) {
    override var hp: Float = 20f
    override var mp: Float = 10f
    override var attack: Float = 0f
    override var strength: Float = 7f
    override var defence: Float = 2f
    override var agility: Float = 2f
    override var evasiveness: Float = 2f
    override var accuracy: Float = 2f
    override var criticalChance: Float = 0.3f
    override var luck: Float = 2f
    override var attackSpeed: Double = 1.0
    override var movementSpeed: Double = 1.0
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE
    override var experience: Float = 0f

    init {
        initialize("Player")
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
    override var texture: TextureAtlas.AtlasRegion = Constants.DefaultItemTexture.findRegion("knife")

    override val defaultAnimationName: String = "buddy"
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override val textureHaver: TextureHaver = this

    override var animation: Animation<TextureRegion>? = null

    var equipmentSize: Int = 96
        private set(value) {
            field = value
            equipmentSizeChanged = true
        }
    var equipmentSizeChanged: Boolean = false

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

    fun showPickedUpItem(item: Item) {
        val itemActor = Image(item.texture)
        itemActor.setSize(itemActor.width * 4, itemActor.height * 4)
        itemActor.name = "item"

        this.addActor(itemActor)
        itemActor.setPosition(0f, this.height)
        itemActor.addAction(Actions.moveBy(0f, 36f, 1f))
        itemActor.addAction(
            Actions.sequence(
            Actions.fadeOut(1.25f),
            Actions.removeActor()))
    }
}