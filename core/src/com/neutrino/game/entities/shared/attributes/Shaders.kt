package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.graphics.drawing.layers.LayeredTexture
import com.neutrino.game.graphics.shaders.ShaderParametered

class Shaders(
    private vararg val initShaders: ShaderParametered
): Attribute() {

    val shaders = object: ArrayList<ShaderParametered>() {
        override fun add(element: ShaderParametered): Boolean {
            getLayeredTextures().forEach { it.addShader(element) }
            return true
        }

        override fun remove(element: ShaderParametered): Boolean {
            getLayeredTextures().forEach { it.removeShader(element) }
            return true
        }

        override fun addAll(elements: Collection<ShaderParametered>): Boolean {
            super.addAll(elements)
            val textures = getLayeredTextures()
            elements.forEach { shader -> textures.forEach { it.addShader(shader) } }
            return true
        }

        override fun clear() {
            for (i in indices.reversed()) {
                remove(get(i))
            }
        }
    }

    override fun onEntityAttached() {
        this.shaders.addAll(initShaders)
    }

    override fun onEntityDetached() {
        shaders.clear()
    }

    private fun getLayeredTextures(): List<LayeredTexture> {
        return entity.get(LayeredDraws::class)?.getBaseTextures() ?: listOf()
    }
}