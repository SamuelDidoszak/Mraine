package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
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
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillBleed
import com.neutrino.game.domain.model.systems.skills.SkillCripplingSpin
import com.neutrino.game.domain.model.systems.skills.SkillTeleport
import com.neutrino.game.domain.model.systems.skills.passive.IncreaseTwohandedDamage
import com.neutrino.game.domain.model.turn.Turn
import kotlin.reflect.KClass

object Player : Character(0, 0, 0.0), HasInventory, HasEquipment, HasSkills, HasPassives {
    override var hp: Float
        get() = super.hp
        set(value) {
            val previous = hp
            super.hp = value
            if (getTag(CharacterTag.BerserkLowerHpHigherDmg::class) != null) {
                val tag = getTag(CharacterTag.BerserkLowerHpHigherDmg::class)
                if (hp < hpMax * tag!!.hpPercentThreshold || previous < hpMax * tag.hpPercentThreshold)
                    sendStatChangeData(StatsEnum.DAMAGE)
            }
            // Send true if hp decreased, false otherwise
            GlobalData.notifyObservers(GlobalDataType.PLAYERHP, value.compareDelta(previous))}
    override var mp: Float
        get() = super.mp
        set(value) {
            val previous = mp
            super.mp = value
            GlobalData.notifyObservers(GlobalDataType.PLAYERMANA, value.compareDelta(previous))}

    override var hpMax: Float
        get() = super.hpMax
        set(value) {
            super.hpMax = value
            sendStatChangeData(StatsEnum.HP_MAX)}
    override var mpMax: Float
        get() = super.mpMax
        set(value) {
            super.mpMax = value
            sendStatChangeData(StatsEnum.MP_MAX)}
    override var strength: Float
        get() = super.strength
        set(value) {
            super.strength = value
            sendStatChangeData(StatsEnum.STRENGTH)}
    override var dexterity: Float
        get() = super.dexterity
        set(value) {
            super.dexterity = value
            sendStatChangeData(StatsEnum.DEXTERITY) }
    override var intelligence: Float
        get() = super.intelligence
        set(value) {
            super.intelligence = value
            sendStatChangeData(StatsEnum.INTELLIGENCE) }
    override var luck: Float
        get() = super.luck
        set(value) {
            super.luck = value
            sendStatChangeData(StatsEnum.LUCK) }

    override var damage: Float
        get() = super.damage
        set(value) {
            super.damage = value
            sendStatChangeData(StatsEnum.DAMAGE)}
    override var damageVariation: Float
        get() = super.damageVariation
        set(value) {
            super.damageVariation = value
            sendStatChangeData(StatsEnum.DAMAGE_VARIATION)}
    override var defence: Float
        get() = super.defence
        set(value) {
            super.defence = value
            sendStatChangeData(StatsEnum.DEFENCE)}

    override var criticalChance: Float = 0.3f
        set(value) {
        field = value
        sendStatChangeData(StatsEnum.CRITICAL_CHANCE)}
    override var criticalDamage: Float = 2f
        set(value) {field = value
        sendStatChangeData(StatsEnum.CRITICAL_DAMAGE)}

    override var movementSpeed: Double = 1.0
        set(value) {field = value
        sendStatChangeData(StatsEnum.MOVEMENT_SPEED)}
    override var attackSpeed: Double = 1.0
        set(value) {field = value
        sendStatChangeData(StatsEnum.ATTACK_SPEED)}

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
        sendStatChangeData(StatsEnum.RANGE_TYPE)}
    override var stealth: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.STEALTH)}

    override var fireDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.FIRE_DAMAGE)}
    override var waterDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.WATER_DAMAGE)}
    override var earthDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.EARTH_DAMAGE)}
    override var airDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.AIR_DAMAGE)}
    override var poisonDamage: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.POISON_DAMAGE)}

    override var fireDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.FIRE_DEFENCE)}
    override var waterDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.WATER_DEFENCE)}
    override var earthDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.EARTH_DEFENCE)}
    override var airDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.AIR_DEFENCE)}
    override var poisonDefence: Float = 0f
        set(value) {field = value
        sendStatChangeData(StatsEnum.POISON_DEFENCE)}

    override var experience: Float = 0f
        set(value) {field = value
        GlobalData.registerData(GlobalDataType.PLAYEREXP, true)}
    var level: Int = 1
        set(value) {field = value
        GlobalData.registerData(GlobalDataType.PLAYERSTAT, "level")}

    override val skillList: ArrayList<Skill> = object : ArrayList<Skill>(5) {
        override fun add(element: Skill): Boolean {
            super.add(element)
            GlobalData.notifyObservers(GlobalDataType.PLAYERNEWSKILL)
            return true
        }
    }

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

    override val passives: HashMap<KClass<out Skill.PassiveSkill>, Skill.PassiveSkill> = HashMap()

    init {
        strength = 1f
        intelligence = 10f
        hpMax = 30f
        mpMax = 10f
        damage = 3f
        damageVariation = 1f
        criticalChance = 0.05f
        criticalDamage = 2f
        movementSpeed = 1.0
        attackSpeed = 1.0
        range = 1


        initialize("Player")
        // TODO maybe delete it entirely. Each character would have to check if infogroup != null tho
        val infoGroup = findActor<Group>("infoGroup")
        infoGroup.isVisible = false
        inventory.size = 30

        skillList.add(SkillBleed(this))
        skillList.add(SkillCripplingSpin(this))
        skillList.add(SkillTeleport(this))
//        skillList.add(SkillTeleportBackstab(this))
//        skillList.add(SkillManaDrain(this))
//        skillList.add(SkillMeteorite(this))
//        skillList.add(SkillShieldBash(this))
//        skillList.add(SkillTwoshot(this))

//        for (i in 0 .. 100)
//            skillList.add(SkillMeteorite(this))

        println("Skill size ${skillList.size}")

//        val manaRegen = CharacterEvent(Player, TimedEvent(0.0, 0.3, Int.MAX_VALUE, EventManaRegen(Player, 0.1f)), Turn.turn)
//        EventDispatcher.dispatchEvent(manaRegen)

        addTag(CharacterTag.IncreaseOnehandedDamage(400f))
        addTag(CharacterTag.IncreaseStealthDamage(1.5f))

        addPassive(IncreaseTwohandedDamage(this, 1.1f))

        addTag(CharacterTag.BerserkLowerHpHigherDmg(0.8f, 2f))

//        val event = (eventArray.find { it == manaRegen }?.event as EventManaRegen?)
//        event?.power = 5f
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