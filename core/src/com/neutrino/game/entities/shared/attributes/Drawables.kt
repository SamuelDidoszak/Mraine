package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.graphics.drawing.drawables.Drawable
import com.neutrino.game.graphics.drawing.drawables.DrawableTexture

class Drawables: Attribute() {
    private val drawableArray = ArrayList<Drawable>()

    fun addDrawable(drawable: Drawable) {
        drawableArray.add(drawable)
    }

    fun removeDrawable(drawable: Drawable) {
        drawableArray.remove(drawable)
    }

    fun removeDrawable(predicate: (Drawable) -> Boolean): Drawable? {
        val drawable = drawableArray.find { predicate.invoke(it) }
        drawableArray.remove(drawable)
        return drawable
    }

    fun getDrawables(): List<Drawable> {
        return drawableArray
    }

    fun getBaseTextures(): List<DrawableTexture> {
        val textureNames = entity.get(Texture::class)!!.textures.map { it.texture.name }
        return drawableArray.filter { drawable ->
            drawable is DrawableTexture &&
            textureNames.any { it == drawable.texture.texture.name }
        } as List<DrawableTexture>
    }

    fun getAdditionalDrawables(): List<Drawable> {
        val textureNames = entity.get(Texture::class)!!.textures.map { it.texture.name }
        return drawableArray.filter { layeredDraw ->
            layeredDraw is DrawableTexture &&
                    textureNames.all { it != layeredDraw.texture.texture.name }
        } as List<DrawableTexture>
    }

    fun getHeight(): Float {
        var maxHeight = 0f
        for (draw in drawableArray) {
            if (draw.height + draw.yOffset > maxHeight)
                maxHeight = draw.height + draw.yOffset
        }
        return maxHeight
    }
}