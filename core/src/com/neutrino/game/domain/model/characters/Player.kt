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
import com.neutrino.game.domain.model.characters.utility.HasEquipment
import com.neutrino.game.domain.model.characters.utility.HasInventory
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Equipment
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.lessThanDelta

object Player : Character(0, 0, 0.0), HasInventory, HasEquipment {
    override var hp: Float = 0f
        set(value) {
            val previous = hp
            field = value
            // Send true if hp decreased, false otherwise
            GlobalData.notifyObservers(GlobalDataType.PLAYERHP, value.lessThanDelta(previous))}
    override var mp: Float = 0f
        set(value) {field = value
            GlobalData.notifyObservers(GlobalDataType.PLAYERMANA, false)}

    override var hpMax: Float = 30f
        set(value) {field = value
            sendStatChangeData(StatsEnum.HPMAX)}
    override var mpMax: Float = 10f
        set(value) {field = value
            sendStatChangeData(StatsEnum.MPMAX)}
    override var strength: Float = 0f
        set(value) {
            val difference = value - strength
            field = value
            hpMax += difference * 5f
            hp += difference * 5f
            damage += difference * 0.5f
            sendStatChangeData(StatsEnum.STRENGTH)}
    override var dexterity: Float = 0f
        set(value) {
            val difference = value - dexterity
            field = value
            evasion += difference * 0.015f
            accuracy += difference * 0.02f
            stealth += difference * 0.015f
            sendStatChangeData(StatsEnum.DEXTERITY)}
    override var intelligence: Float = 0f
        set(value) {
            val difference = value - intelligence
            field = value
            mpMax += difference * 5f
            mp += difference * 5f
            sendStatChangeData(StatsEnum.INTELLIGENCE)}
    override var luck: Float = 0f
        set(value) {
            val difference = value - luck
            field = value
            criticalChance += difference * 0.015f
            sendStatChangeData(StatsEnum.LUCK)}

    override var damage: Float = 3f
        set(value) {field = value
        sendStatChangeData(StatsEnum.DAMAGE)}
    override var damageVariation: Float = 1f
        set(value) {field = value
        sendStatChangeData(StatsEnum.DAMAGEVARIATION)}
    override var defence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.DEFENCE)}

    override var criticalChance: Float = 0.3f
        set(value) {field = value
        sendStatChangeData(StatsEnum.CRITICALCHANCE)}
    override var criticalDamage: Float = 2f
        set(value) {field = value
        sendStatChangeData(StatsEnum.CRITICALDAMAGE)}

    override var movementSpeed: Double = 1.0
        set(value) {field = value
        sendStatChangeData(StatsEnum.MOVEMENTSPEED)}
    override var attackSpeed: Double = 1.0
        set(value) {field = value
        sendStatChangeData(StatsEnum.ATTACKSPEED)}

    override var accuracy: Float = 1f
        set(value) {field = value
        sendStatChangeData(StatsEnum.ACCURACY)}
    override var evasion: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.EVASION)}

    override var range: Int = 1
        set(value) {field = value
        sendStatChangeData(StatsEnum.RANGE)}
    override var rangeType: RangeType = RangeType.SQUARE
        set(value) {field = value
        sendStatChangeData(StatsEnum.RANGETYPE)}
    override var stealth: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.STEALTH)}

    override var fireDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.FIREDAMAGE)}
    override var waterDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.WATERDAMAGE)}
    override var earthDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.EARTHDAMAGE)}
    override var airDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.AIRDAMAGE)}
    override var poisonDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.POISONDAMAGE)}

    override var fireDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.FIREDEFENCE)}
    override var waterDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.WATERDEFENCE)}
    override var earthDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.EARTHDEFENCE)}
    override var airDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.AIRDEFENCE)}
    override var poisonDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.POISONDEFENCE)}

    override var experience: Float = 0f
        set(value) {field = value
        GlobalData.registerData(GlobalDataType.PLAYEREXP, true)}
    var level: Int = 1
        set(value) {field = value
        GlobalData.registerData(GlobalDataType.PLAYERSTAT, "level")}

    val skills = ArrayList<Int>()

    /** Determines the maximum number of concurrently used skills that do not use mana */
    var maxSkills: Int = 3



    override fun setTexture(name: String) {
        texture = getTexture(name)
        setBounds(xPos * 64f, parent.height - yPos * 64f, textureHaver.texture.regionWidth.toFloat() * 4, textureHaver.texture.regionHeight.toFloat() * 4)
        height = textureHaver.texture.regionHeight.toFloat() * 4
    }

    // Inventory
    /** Player inventory */
    override val inventory: Inventory = Inventory()
    override val equipment: Equipment = Equipment(this)

    init {
        initialize("Player")
        // TODO maybe delete it entirely. Each character would have to check if infogroup != null tho
        val infoGroup = findActor<Group>("infoGroup")
        infoGroup.isVisible = false
        inventory.size = 30
    }

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

    fun addToInventory(item: Item): Boolean {
        // add to stack
        if (item.amount != null) {
            val stackableItem = inventory.itemList.find { it.item.name == item.name }
            if (stackableItem != null) {
                stackableItem.item.amount = stackableItem.item.amount!!.plus(item.amount!!)
                GlobalData.notifyObservers(GlobalDataType.PICKUP, stackableItem.item)
                return true
            }
        }
        if (inventory.itemList.size < inventory.size) {
            inventory.itemList.add(EqElement(item, Turn.turn))
            GlobalData.notifyObservers(GlobalDataType.PICKUP, item)
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

    private fun sendStatChangeData(stat: StatsEnum) {
        GlobalData.registerData(GlobalDataType.PLAYERSTAT, stat)
    }
}