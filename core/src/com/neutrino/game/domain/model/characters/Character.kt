package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.domain.model.entities.utility.TextureHaver

abstract class Character(
    var xPos: Int,
    var yPos: Int
): Actor(), TextureHaver {
    override fun setName(name: String?) {
        this.name = name
    }
    abstract val description: String

    abstract val textureHaver: TextureHaver

    override fun setTexture(name: String) {
        super.setTexture(name)
        setBounds(xPos * 16f, parent.height - yPos * 16f, textureHaver.texture.regionWidth.toFloat(), textureHaver.texture.regionHeight.toFloat())
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch != null) {
            batch.draw(textureHaver.texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        } else {
            // TODO add a default character texture
        }
    }
}