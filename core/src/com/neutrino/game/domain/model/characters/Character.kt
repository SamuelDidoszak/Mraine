package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants.MoveSpeed
import com.neutrino.game.domain.model.characters.utility.Ai
import com.neutrino.game.domain.model.characters.utility.HpBar
import com.neutrino.game.domain.model.characters.utility.Stats
import com.neutrino.game.domain.model.entities.utility.TextureHaver

abstract class Character(
    var xPos: Int,
    var yPos: Int,
    var turn: Double
): Group(), TextureHaver, Stats {
    init {
        val infoGroup = Group()
        this.addActor(infoGroup)
        infoGroup.name = "infoGroup"
        infoGroup.setPosition(0f, 32f * 4)

        val nameLabel = TextraLabel("", KnownFonts.getStandardFamily())
        nameLabel.name = "name"
        infoGroup.addActor(nameLabel)
    }

    /**
     * This method is called only once, after initialization of Character implementation
     * Passing values here makes sure that they are initialized
     */
    override fun setName(name: String?) {
        super.setName(name)
        this.findActor<TextraLabel>("name").setText("[@Cozette][GREEN][%175]$name")
        (this.getChild(0) as Group).addActor(HpBar(currentHp, hp))
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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (batch != null) {
            batch.draw(textureHaver.texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        } else {
            // TODO add a default character texture
        }
    }

    fun move(xPos: Int, yPos: Int) {
        this.addAction(Actions.moveTo(xPos * 64f, parent.height - yPos * 64f, MoveSpeed))
        this.xPos = xPos
        this.yPos = yPos
    }

    fun getDamage(character: Character): Float {
        var damage: Float = 0f
        damage += character.attack - defence
        damage = if (damage < 0) 0f else damage
        damage = 4f
        this.currentHp -= damage
        if (currentHp <= 0) {
            parent.removeActor(this)
            currentHp = 0f
        }
        this.findActor<HpBar>("hpBar").currentHp = currentHp
        return damage
    }
}