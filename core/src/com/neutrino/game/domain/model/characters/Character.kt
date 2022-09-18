package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.domain.model.characters.utility.*
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.utility.ColorUtils
import java.awt.Color
import kotlin.math.round
import kotlin.random.Random

abstract class Character(
    var xPos: Int,
    var yPos: Int,
    var turn: Double
): Group(), TextureHaver, Stats, Randomization {
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
    override fun setName(name: String?) {
        super.setName(name)
        this.findActor<TextraLabel>("name").setText("[@Cozette][WHITE][%175]$name")
        (this.getChild(0) as Group).addActor(HpBar(currentHp, hp))

        val turnBar = TurnBar(this.turn + this.movementSpeed, Player.turn + Player.movementSpeed, this.movementSpeed)
        this.findActor<Group>("infoGroup").addActor(turnBar)
    }

    abstract val description: String

    abstract val textureHaver: TextureHaver

    val ai: Ai = Ai(this)

    override fun setTexture(name: String) {
        super.setTexture(name)
        setBounds(xPos * 64f, parent.height - yPos * 64f, textureHaver.texture.regionWidth.toFloat() * 4, textureHaver.texture.regionHeight.toFloat() * 4)
        val nameLabel = (this.getChild(0) as Group).getChild(0)
        nameLabel.setPosition(nameLabel.x + 32 - nameLabel.width / 2, nameLabel.y)
    }

    fun updateTurnBar() {
        val turnBar =  this.findActor<TurnBar>("turnBar")
        turnBar.currentTurn = this.turn
        turnBar.playerTurn = Player.turn

        var size: Float = ((this.turn - Player.turn)/ this.movementSpeed).toFloat()
        turnBar.addAction(Actions.sizeTo(size * 60f, 2f, MoveSpeed))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        if (batch != null) {
            batch.draw(textureHaver.texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        } else {
            // TODO add a default character texture
        }
        super.draw(batch, parentAlpha)
        color.a = 1f
        batch?.color = color

        val turnBar =  this.findActor<TurnBar>("turnBar")
        turnBar.draw(batch, parentAlpha)
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

        val damageLabel = TextraLabel("[@Cozette][${colorUtils.toHexadecimal(damageColor)}][%150][*]{SQUASH=0.3;false}" +
                "${round(damage).toInt()}", KnownFonts.getStandardFamily())
        damageLabel.name = "damage"
        this.addActor(damageLabel)
        damageLabel.setPosition(Random.nextFloat() * this.width * 0.8f, Random.nextFloat() * this.height / 3 + this.height / 4)
        damageLabel.addAction(Actions.moveBy(0f, 36f, 1f))
        damageLabel.addAction(Actions.sequence(
            Actions.fadeOut(1.25f),
            Actions.removeActor()))

        this.currentHp -= damage
        if (currentHp <= 0) {
//            this.addAction(Actions.fadeOut(1.25f))
            this.addAction(Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()
            ))
            currentHp = 0f
        }
        this.findActor<HpBar>("hpBar").currentHp = currentHp
        return damage
    }
}