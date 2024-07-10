package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.util.Constants.SCALE_INT
import kotlin.math.roundToInt

open class DrawableTexture(
    entity: Entity,
    val texture: TextureSprite
): Drawable() {
    private var sizeScale: Float = if (entity == Player) 2.5f else if (entity is Character) 2f else SCALE_INT.toFloat()
    var scale: Float = 1f
        set(value) {
            sizeScale /= field
            sizeScale *= value
            field = value
        }
    init {
        this.entity = entity
        drawPosition = entity.get(DrawPosition::class)!!
    }

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        batch.setAlpha(parentAlpha * alpha)
        width = (texture.texture.regionWidth * sizeScale).roundToInt()
        height = (texture.texture.regionHeight * sizeScale).roundToInt()
        batch.draw(texture.texture,
            if (!texture.mirrorX) x + getX() else x + getX() +
                    if (texture !is AnimatedTextureSprite) width else (texture.mirrorPivot * sizeScale).roundToInt(),
            y + getY(),
            width * if (!texture.mirrorX) 1f else -1f,
            height * 1f)
    }

    /** Returns scaled x position including map placement */
    override fun getX(): Float {
        return drawPosition.x + xOffset + texture.x * sizeScale
    }

    /** Returns scaled y position including map placement */
    override fun getY(): Float {
        return drawPosition.y + yOffset + texture.y * sizeScale
    }

    override fun getYSort(): Float {
        return getY()
    }

    /**
     * Set width only by using scale
     */
    override var width: Int
        get() = ((texture.width() + texture.x) * sizeScale).toInt()
        set(value) {}

    /**
     * Set height only by using scale
     */
    override var height: Int
        get() = ((texture.height() + texture.y) * sizeScale).toInt()
        set(value) {}
}