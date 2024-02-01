package com.neutrino.game.graphics.drawing

import com.neutrino.game.util.Constants.SCALE_INT
import com.neutrino.game.entities.Entity
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.map.attributes.DrawPosition

internal open class LayeredTexture(
    val entity: Entity,
    val texture: TextureSprite
) {
    private val drawPositionAttribute = entity.get(DrawPosition::class)!!

    /** Returns scaled x position including map placement */
    fun getX(): Float {
        return drawPositionAttribute.x + texture.x * SCALE_INT * if (texture.mirrorX) -1 else 1
    }

    /** Returns scaled y position including map placement */
    fun getY(): Float {
        return drawPositionAttribute.y + texture.y * SCALE_INT
    }

    fun getYPos(): Float {
        return drawPositionAttribute.y
    }

    open fun getYSort(): Float {
        return getY()
    }

    /** Returns scaled width */
    fun getWidth(): Int {
        return texture.texture.regionWidth * SCALE_INT
    }

    /** Returns scaled height */
    fun getHeight(): Int {
        return texture.texture.regionHeight * SCALE_INT
    }

    operator fun compareTo(value: LayeredTexture): Int {
        return compareValues(getY(), value.getY())
    }
}