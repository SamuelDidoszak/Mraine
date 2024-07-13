package com.neutrino.game.graphics.drawing

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Drawables
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.drawing.drawables.DrawableTexture
import com.neutrino.game.graphics.textures.AnimatedTextureSprite

class Animations(val drawer: EntityDrawer) {
    private val animations = ArrayList<DrawableTexture>(10)
    private val nextAnimations = ArrayList<DrawableTexture>()

    val size: Int
        get() = animations.size

    fun add(animation: DrawableTexture) {
        animations.add(animation)
    }

    fun remove(animation: DrawableTexture) {
        animations.remove(animation)
    }

    fun remove(entity: Entity) {
        animations.removeIf { it.entity == entity }
    }

    fun update(deltaTime: Float) {
        play(deltaTime)
    }

    private val DrawableTexture.animation: AnimatedTextureSprite
        get() = this.texture as AnimatedTextureSprite

    fun play(deltaTime: Float) {
        val iterator = animations.listIterator()
        while (iterator.hasNext()) {
            val animation = iterator.next()
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    drawer.lights.remove(animation to light)
                }
            }
            val remove = !animation.animation.setFrame(deltaTime)
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    drawer.lights.add(animation to light)
                }
            }
            if (remove && (animation.animation.nextAnimation != null || animation.animation.deleteAfterEnd)) {
                iterator.remove()
                if (animation.animation.nextAnimation != null)
                    nextAnimations.add(animation)
                else
                    animation.detach()
            }
        }

        for (animation in nextAnimations) {
            animation.entity.get(Texture::class)!!
                .textures[animation.entity.get(Texture::class)!!.textures.indexOf(animation.animation)] = animation.animation.nextAnimation!!
        }
        nextAnimations.clear()
    }

    fun clear() {
        animations.clear()
    }
}