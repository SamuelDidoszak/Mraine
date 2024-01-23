package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.map.attributes.OnMapPosition
import com.neutrino.game.graphics.drawing.AnimationData
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.TextureSprite
import kotlin.random.Random

class Texture(
    private val setTextures: (position: OnMapPosition?,
                              random: Random,
                              textures: ArrayList<TextureSprite>) -> Unit
): Attribute() {
    val textures: TextureList = TextureList()

    fun setTextures(onMapPosition: OnMapPosition?, randomGenerator: Random) {
        setTextures.invoke(
            onMapPosition ?: (entity get OnMapPosition::class),
            randomGenerator,
            textures)
    }

    /**
     * Sets the animation for textures[0]
     */
    fun setAnimation(animation: AnimatedTextureSprite, nextAnimation: AnimatedTextureSprite?, index: Int = 0) {
        textures.set(index, animation, AnimationData(animation, entity, nextAnimation))
    }

    fun finalize() {
        textures.removeIf { true }
    }

    inner class TextureList: ArrayList<TextureSprite>(1) {

        override fun set(index: Int, element: TextureSprite): TextureSprite {
            val oldElement = super.set(index, element)
            removeFromLevel(oldElement)
            addToLevel(element)
            return oldElement
        }

        fun set(index: Int, element: AnimatedTextureSprite, animationData: AnimationData): TextureSprite {
            val oldElement = super.set(index, element)
            removeFromLevel(oldElement)
            addToLevel(element, animationData)
            return oldElement
        }

        override fun add(element: TextureSprite): Boolean {
            addToLevel(element)
            return super.add(element)
        }

        override fun addAll(elements: Collection<TextureSprite>): Boolean {
            elements.forEach { addToLevel(it) }
            return super.addAll(elements)
        }

        override fun addAll(index: Int, elements: Collection<TextureSprite>): Boolean {
            elements.forEach { addToLevel(it) }
            return super.addAll(index, elements)
        }

        override fun remove(element: TextureSprite): Boolean {
            removeFromLevel(element)
            return super.remove(element)
        }

        override fun removeAt(index: Int): TextureSprite {
            val element = super.removeAt(index)
            removeFromLevel(element)
            return element
        }

        override fun clear() {
            for (i in (0 until size).reversed())
                removeAt(i)
        }

        private fun addToLevel(element: TextureSprite, animationData: AnimationData? = null) {
            val level = entity.get(OnMapPosition::class)!!.level
            if (element.z != 0)
                level.addTexture(entity, element)
            if (element is AnimatedTextureSprite)
                level.animations.add(animationData ?: AnimationData(element, entity))
            if (element.lights != null) {
                if (element.lights!!.isSingleLight)
                    level.lights.add(Pair(entity, element.lights!!.getLight().xyDiff(element.x, element.y)))
                else {
                    if (element is AnimatedTextureSprite) {
                        for (i in 0 until element.lights!!.getLightArraySize()) {
                            for (light in element.lights!!.getLights(i)!!) {
                                if (element.mirrorX)
                                    light.x += element.width() + element.x * -1
                                else
                                    light.x += element.x
                                light.y += element.y
                            }
                        }
                    } else {
                        for (light in element.lights!!.getLights()!!) {
                            if (element.mirrorX)
                                light.x += element.width() + element.x * -1
                            else
                                light.x += element.x
                            light.y += element.y
                            level.lights.add(Pair(entity, light))
                        }
                    }
                }
            }
        }

        private fun removeFromLevel(element: TextureSprite) {
            val level = entity.get(OnMapPosition::class)!!.level
            if (element.z != 0)
                level.removeTexture(entity, element)
            if (element is AnimatedTextureSprite)
                level.animations.remove(AnimationData(element, entity))
            if (element.lights != null) {
                if (element.lights!!.isSingleLight)
                    level.lights.remove(Pair(entity, element.lights!!.getLight()))
                else
                    for (light in element.lights!!.getLights()!!) {
                        level.lights.remove(Pair(entity, light))
                    }
            }
        }
    }
}