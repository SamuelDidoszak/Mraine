package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.equipment.utility.EqElement
import com.neutrino.game.domain.model.items.equipment.utility.Inventory
import com.neutrino.game.domain.model.turn.Turn

object Player : Character(0, 0, 0.0) {
    override var hpMax: Float = 20f
    override var mpMax: Float = 10f
    override var strength: Float = 4.5f
    override var dexterity: Float = 2f
    override var intelligence: Float = 0f
    override var luck: Float = 2f
    override var damage: Float = 0f
    override var damageVariation: Float = 1f
    override var defence: Float = 2f
    override var criticalChance: Float = 0.3f
    override var experience: Float = 0f

    init {
        initialize("Player")
        damage = setDamage()
    }

    // Inventory
    /** Player inventory */
    val inventory: Inventory = Inventory()
    val equipped: Inventory = Inventory()

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

    var inventorySize: Int = 30
        private set(value) {
            field = value
            inventorySizeChanged = true
        }
    var inventorySizeChanged: Boolean = false

    fun addToInventory(item: Item): Boolean {
        // add to stack
        if (item.stackable) {
            val stackableItem = inventory.itemList.find { it.item.name == item.name }
            if (stackableItem != null) {
                stackableItem.item.amount = stackableItem.item.amount!!.plus(item.amount!!)
                return true
            }
        }
        if (inventory.itemList.size < inventorySize) {
            inventory.itemList.add(EqElement(item, Turn.turn))
            return true
        }
        return false
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