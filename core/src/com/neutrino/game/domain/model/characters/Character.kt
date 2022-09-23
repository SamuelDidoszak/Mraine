package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Pools
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.domain.model.characters.utility.*
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.CooldownType
import com.neutrino.game.domain.model.turn.Event
import com.neutrino.game.domain.model.utility.ColorUtils
import java.awt.Color
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class Character(
    var xPos: Int,
    var yPos: Int,
    var turn: Double
): Group(), TextureHaver, Animated, Stats, Randomization {
    /** List of item drops */
    open val possibleItemDropList: List<Pair<KClass<Item>, Double>> = listOf()
    private val itemtemDropList: MutableList<Item> = ArrayList()
    init {
        val infoGroup = Group()
        this.addActor(infoGroup)
        infoGroup.name = "infoGroup"
        infoGroup.setPosition(0f, 32f * 4)

        val nameLabel = TextraLabel("", KnownFonts.getStandardFamily())
        nameLabel.name = "name"
        infoGroup.addActor(nameLabel)
    }

    override val randomizationProbability: Float = 0.3f
    override var randomizationMultiplier: Float = 0f

    /**
     * This method is called only once, after initialization of Character implementation
     * Passing values here makes sure that they are initialized
     */
    fun initialize(name: String?) {
        super.setName(name)
        this.findActor<TextraLabel>("name").setText("[@Cozette][WHITE][%175]$name")
        (this.getChild(0) as Group).addActor(HpBar(currentHp, hp))

        val turnBar = TurnBar(this.turn + this.movementSpeed, Player.turn + Player.movementSpeed, this.movementSpeed)
        this.findActor<Group>("infoGroup").addActor(turnBar)

        // TODO if unkillable characters will be added, add this::class check

        possibleItemDropList.forEach {
            if (Constants.RandomGenerator.nextDouble() < it.second)
                itemtemDropList.add(it.first.createInstance())
        }
    }

    abstract val description: String

    abstract val textureSrc: String
    override val textureHaver: TextureHaver = this

    val ai: Ai = Ai(this)
    val eventArray: MutableList<com.neutrino.game.domain.model.turn.Event> = ArrayList()
    fun hasCooldown(cooldownType: CooldownType): Boolean {
        return eventArray.find { it is Event.COOLDOWN && it.cooldownType == cooldownType } != null
    }

    override var textureList:  List<TextureAtlas.AtlasRegion> = listOf()
    override fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureNames) {
                add(atlas.findRegion(name))
            } }
    }

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return textureList.find { it.name.toString() == name }!!
    }

    override fun setTexture(name: String) {
        texture = getTexture(name)
        setBounds(xPos * 64f, parent.height - yPos * 64f, textureHaver.texture.regionWidth.toFloat() * 4, textureHaver.texture.regionHeight.toFloat() * 4)
        val nameLabel = (this.getChild(0) as Group).getChild(0)
        nameLabel.setPosition(nameLabel.x + 32 - nameLabel.width / 2, nameLabel.y)
    }

    fun updateTurnBar(forceUpdateMovementColor: Boolean = false) {
        val turnBar =  this.findActor<TurnBar>("turnBar")
        turnBar.update(this.turn, Player.turn, this.movementSpeed, forceUpdateMovementColor)

        val size: Float = ((this.turn - Player.turn)/ this.movementSpeed).toFloat()
        turnBar.addAction(Actions.sizeTo(size * 60f, 2f, MoveSpeed))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        if (batch != null) {
            batch.draw(textureHaver.texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        } else {
            // TODO add a default character texture
        }
        super.draw(batch, parentAlpha)

        color.a = 1f
        batch?.color = color
    }

    fun move(xPos: Int, yPos: Int, speed: Float = MoveSpeed) {
        this.addAction(Actions.moveTo(xPos * 64f, parent.height - yPos * 64f, speed))
        this.xPos = xPos
        this.yPos = yPos
    }

    fun getDamage(character: Character): Float {
        var damage: Float = 0f
        var physicalDamage = character.attack - defence
        physicalDamage = if (physicalDamage < 0) 0f else physicalDamage
        val fireDamage = character.fireDamage * (1 - fireDefence)
        val waterDamage = character.waterDamage * (1 - waterDefence)
        val earthDamage = character.earthDamage * (1 - earthDefence)
        val airDamage = character.airDamage * (1 - airDefence)
        val poisonDamage = character.poisonDamage * (1 - poisonDefence)

        damage += physicalDamage
        damage += fireDamage
        damage += waterDamage
        damage += earthDamage
        damage += airDamage
        damage += poisonDamage

        // for optimization, instead of creating Colors, pure rgb values can be passed
        val colorUtils = ColorUtils()

        // get damage color from interpolation
        var damageColor: Color = Color(0, 0, 0)
        damageColor = colorUtils.colorInterpolation(damageColor, Color(255, 0, 0), (physicalDamage / damage).toInt())
        damageColor = colorUtils.colorInterpolation(damageColor, Color(255, 128, 0), (fireDamage / damage).toInt())
        damageColor = colorUtils.colorInterpolation(damageColor, Color(0, 0, 255), (waterDamage / damage).toInt())
        damageColor = colorUtils.colorInterpolation(damageColor, Color(0, 255, 0), (earthDamage / damage).toInt())
        damageColor = colorUtils.colorInterpolation(damageColor, Color(0, 255, 255), (airDamage / damage).toInt())
        damageColor = colorUtils.colorInterpolation(damageColor, Color(128, 255, 0), (poisonDamage / damage).toInt())

        damageColor = colorUtils.applySaturation(damageColor, 0.8f)

        // Old damage particle/number system

//        val damageLabel = Label(round(damage).toInt().toString(), Scene2DSkin.defaultSkin)
//        damageLabel.color = com.badlogic.gdx.graphics.Color(damageColor.red.toFloat(),
//            damageColor.green.toFloat(), damageColor.blue.toFloat(), damageColor.alpha.toFloat())
////        val damageLabel = TextraLabel("[@Cozette][${colorUtils.toHexadecimal(damageColor)}][%150][*]{SQUASH=0.3;false}" +
////                "${round(damage).toInt()}", KnownFonts.getStandardFamily())
//        damageLabel.name = "damage"
//        this.addActor(damageLabel)
//        damageLabel.setPosition(Random.nextFloat() * this.width * 0.8f, Random.nextFloat() * this.height / 3 + this.height / 4)
//        damageLabel.addAction(Actions.moveBy(0f, 36f, 1f))
//        damageLabel.addAction(Actions.sequence(
//            Actions.fadeOut(1.25f),
//            Actions.removeActor()))

        val damageNumber = Pools.get(DamageNumber::class.java).obtain()
        this.addActor(damageNumber)
        damageNumber.init(colorUtils.toHexadecimal(damageColor), damage)


        this.currentHp -= damage
        if (currentHp <= 0) {
            this.addAction(Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()
            ))
            currentHp = 0f
        }
        this.findActor<HpBar>("hpBar").update(currentHp)
        return damage
    }

    fun dropItems(): MutableList<Item> {
        return itemtemDropList
    }
}