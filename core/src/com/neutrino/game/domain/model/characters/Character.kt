package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.MoveSpeed
import com.neutrino.game.domain.model.characters.utility.Ai
import com.neutrino.game.domain.model.characters.utility.HpBar
import com.neutrino.game.domain.model.characters.utility.Stats
import com.neutrino.game.domain.model.entities.utility.TextureHaver

abstract class Character(
    var xPos: Int,
    var yPos: Int
): Group(), TextureHaver, Stats {
    init {
        val infoGroup = Group()
        this.addActor(infoGroup)
        infoGroup.name = "infoGroup"
        infoGroup.setPosition(0f, 32f)

        val nameLabel = TextraLabel("[@Cozette][GREEN][%25]$name", KnownFonts.getStandardFamily())
        nameLabel.name = "name"
        infoGroup.addActor(nameLabel)
    }

    open var turn: Double = Double.MAX_VALUE

    /**
     * This method is called only once, after initialization of Character implementation
     * Passing values here makes sure that they are initialized
     */
    override fun setName(name: String?) {
        super.setName(name)
        this.findActor<TextraLabel>("name").setText("[@Cozette][GREEN][%50]$name")
        (this.getChild(0) as Group).addActor(HpBar(currentHp, hp))
    }

    abstract val description: String

    abstract val textureHaver: TextureHaver

    val ai: Ai = Ai(this)

    override fun setTexture(name: String) {
        super.setTexture(name)
        setBounds(xPos * 16f, parent.height - yPos * 16f, textureHaver.texture.regionWidth.toFloat(), textureHaver.texture.regionHeight.toFloat())
        val nameLabel = (this.getChild(0) as Group).getChild(0)
        nameLabel.setPosition(nameLabel.x + 8 - nameLabel.width / 2, nameLabel.y)
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
        this.addAction(Actions.moveTo(xPos * 16f, parent.height - yPos * 16f, MoveSpeed))
        this.xPos = xPos
        this.yPos = yPos
    }

    fun getDamage(character: Character): Float {
        var damage: Float = 0f
        damage += character.attack - defence
        damage = if (damage < 0) 0f else damage
        damage = 4f
        this.currentHp -= damage
        this.findActor<HpBar>("hpBar").currentHp = if (currentHp < 0) 0f else currentHp
        return damage
    }
}