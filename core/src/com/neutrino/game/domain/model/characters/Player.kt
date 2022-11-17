package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Equipment
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.domain.model.turn.Turn

object Player : Character(0, 0, 0.0), HasInventory {
    override var hpMax: Float = 30f
    override var mpMax: Float = 10f
    override var strength: Float = 3f
    override var dexterity: Float = 2f
    override var intelligence: Float = 0f
    override var luck: Float = 2f
    override var damage: Float = 0f
    override var damageVariation: Float = 1f
    override var defence: Float = 0f
    override var criticalChance: Float = 0.3f
    override var experience: Float = 0f
    override var movementSpeed: Double = 1.0
    /** Determines the maximum number of concurrently used skills that do not use mana */
    var maxSkills: Int = 3

    init {
        initialize("Player")
        damage = setDamage()
        // TODO maybe delete it entirely. Each character would have to check if infogroup != null tho
        val infoGroup = findActor<Group>("infoGroup")
        infoGroup.isVisible = false
    }

    override fun setTexture(name: String) {
        texture = getTexture(name)
        setBounds(xPos * 64f, parent.height - yPos * 64f, textureHaver.texture.regionWidth.toFloat() * 4, textureHaver.texture.regionHeight.toFloat() * 4)
        height = textureHaver.texture.regionHeight.toFloat() * 4
    }

    // Inventory
    /** Player inventory */
    override val inventory: Inventory = Inventory()
    val equipment: Equipment = Equipment(this)

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
        if (item.amount != null) {
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
        GlobalData.notifyObservers(GlobalDataType.PICKUP)
    }

    override fun getDamage(character: Character): Float {
        GlobalData.notifyObservers(GlobalDataType.PLAYERHP)
        return super.getDamage(character)
    }
}