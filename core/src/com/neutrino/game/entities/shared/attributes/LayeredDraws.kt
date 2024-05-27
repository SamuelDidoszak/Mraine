package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.graphics.drawing.layers.LayeredDraw
import com.neutrino.game.graphics.drawing.layers.LayeredTexture

class LayeredDraws: Attribute() {
    private val layeredDrawArray = ArrayList<LayeredDraw>()

    fun addLayeredDraw(layeredDraw: LayeredDraw) {
        layeredDrawArray.add(layeredDraw)
    }

    fun removeLayeredDraw(layeredDraw: LayeredDraw) {
        layeredDrawArray.remove(layeredDraw)
    }

    fun removeLayeredDraw(predicate: (LayeredDraw) -> Boolean): LayeredDraw? {
        val layeredDraw = layeredDrawArray.find { predicate.invoke(it) }
        layeredDrawArray.remove(layeredDraw)
        return layeredDraw
    }

    fun getLayeredDraws(): List<LayeredDraw> {
        return layeredDrawArray
    }

    fun getBaseTextures(): List<LayeredTexture> {
        val textureNames = entity.get(Texture::class)!!.textures.map { it.texture.name }
        return layeredDrawArray.filter { layeredDraw ->
            layeredDraw is LayeredTexture &&
            textureNames.any { it == layeredDraw.texture.texture.name }
        } as List<LayeredTexture>
    }

    fun getAdditionalDraws(): List<LayeredDraw> {
        val textureNames = entity.get(Texture::class)!!.textures.map { it.texture.name }
        return layeredDrawArray.filter { layeredDraw ->
            layeredDraw is LayeredTexture &&
                    textureNames.all { it != layeredDraw.texture.texture.name }
        } as List<LayeredTexture>
    }
}