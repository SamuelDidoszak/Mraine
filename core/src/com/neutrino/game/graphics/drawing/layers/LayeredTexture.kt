package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.util.Constants.SCALE_INT

internal open class LayeredTexture(
    entity: Entity,
    val texture: TextureSprite
): LayeredDraw() {
    private val sizeScale = if (entity is Character) 2 else SCALE_INT
    init {
        this.entity = entity
        width = texture.texture.regionWidth * sizeScale
        height = texture.texture.regionHeight * sizeScale
        drawPosition = entity.get(DrawPosition::class)!!
    }

    override fun draw(batch: Batch, x: Float, y: Float, alpha: Float) {
        batch.draw(texture.texture,
            if (!texture.mirrorX) x + getX() else x + getX() + width,
            y + getY(),
            width * if (!texture.mirrorX) 1f else -1f,
            height * 1f)
    }

    /** Returns scaled x position including map placement */
    override fun getX(): Float {
        return drawPosition.x + texture.x * SCALE_INT * if (texture.mirrorX) -1 else 1
    }

    /** Returns scaled y position including map placement */
    override fun getY(): Float {
        return drawPosition.y + texture.y * SCALE_INT
    }

    override fun getYSort(): Float {
        return getY()
    }

//    override var width: Int
//        get() = texture.texture.regionWidth * sizeScale
//        set(value) {}
//
//    override var height: Int
//        get() = texture.texture.regionHeight * sizeScale
//        set(value) {}
}