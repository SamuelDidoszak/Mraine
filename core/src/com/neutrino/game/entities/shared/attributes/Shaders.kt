package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.graphics.drawing.layers.LayeredTexture
import com.neutrino.game.graphics.shaders.ShaderParametered

class Shaders(
    private vararg val initShaders: ShaderParametered
): Attribute() {

    val shaders = object: ArrayList<ShaderParametered>() {
        override fun add(element: ShaderParametered): Boolean {
            super.add(element)
            val textures = getLayeredTextures()
            for (i in textures.indices) {
                if (i > 0 && element is Cloneable<*>)
                    textures[i].addShader(element.clone() as ShaderParametered)
                else
                    textures[i].addShader(element)
            }
            return true
        }

        override fun remove(element: ShaderParametered): Boolean {
            super.remove(element)
            getLayeredTextures().forEach { it.removeShader(element) }
            return true
        }

        override fun addAll(elements: Collection<ShaderParametered>): Boolean {
            elements.forEach { add(it) }
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