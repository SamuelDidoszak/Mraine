package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.EventDispatcher
import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.utility.*
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Equipment
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.domain.model.systems.event.types.EventManaRegen
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.systems.skills.*
import com.neutrino.game.domain.model.turn.Turn

object Player : Character(0, 0, 0.0), HasInventory, HasEquipment, HasSkills {
    override var hp: Float = 0f
        set(value) {
            val previous = hp
            field = value
            // Send true if hp decreased, false otherwise
            GlobalData.notifyObservers(GlobalDataType.PLAYERHP, value.compareDelta(previous))}
    override var mp: Float = 10f
        set(value) {
            val previous = mp
            field = value
            GlobalData.notifyObservers(GlobalDataType.PLAYERMANA, value.compareDelta(previous))}

    override var hpMax: Float = 30f
        set(value) {field = value
            sendStatChangeData(StatsEnum.HPMAX)}
    override var mpMax: Float = 10f
        set(value) {field = value
            sendStatChangeData(StatsEnum.MPMAX)}
    override var strength: Float
        get() = super.strength
        set(value) {
            super.strength = value
            sendStatChangeData(StatsEnum.STRENGTH)}
    override var dexterity: Float
        get() = super.dexterity
        set(value) { sendStatChangeData(StatsEnum.DEXTERITY) }
    override var intelligence: Float
        get() = super.intelligence
        set(value) { sendStatChangeData(StatsEnum.INTELLIGENCE) }
    override var luck: Float
        get() = super.luck
        set(value) { sendStatChangeData(StatsEnum.LUCK) }

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

    override val skillList: ArrayList<Skill> = ArrayList(5)

    /** Determines the maximum number of concurrently used skills that do not use mana */
    override var maxSkills: Int = 3

    override var viewDistance: Int = 20

    override var characterAlignment: CharacterAlignment = CharacterAlignment.PLAYER

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

        skillList.add(SkillBleed(this))
        skillList.add(SkillCripplingSpin(this))
        skillList.add(SkillTeleport(this))
        skillList.add(SkillTeleportBackstab(this))
        skillList.add(SkillManaDrain(this))
        skillList.add(SkillMeteorite(this))

        val manaRegen = CharacterEvent(Player, TimedEvent(0.0, 0.3, Int.MAX_VALUE, EventManaRegen(Player, 0.1f)), Turn.turn)
        EventDispatcher.dispatchEvent(manaRegen)

        addTag(CharacterTag.IncreaseOnehandedDamage(400f))
        addTag(CharacterTag.IncreaseStealthDamage(1.5f))
        addTag(CharacterTag.Lifesteal(1f))

        strength = 3f
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

    override fun move(xPos: Int, yPos: Int, speed: Float) {
        super.move(xPos, yPos, speed)
        GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
    }

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

    private fun sendStatChangeData(stat: StatsEnum) {
        GlobalData.registerData(GlobalDataType.PLAYERSTAT, stat)
    }
}