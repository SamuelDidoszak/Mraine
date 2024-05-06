package com.neutrino.game.graphics.drawing

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Texture

class Animations(val drawer: EntityDrawer) {
    private val animations = ArrayList<AnimationData>(10)
    private val nextAnimations = ArrayList<AnimationData>()

    fun add(animation: AnimationData) {
        animations.add(animation)
    }

    fun remove(animation: AnimationData) {
        animations.remove(animation)
    }

    fun remove(entity: Entity) {
        animations.removeIf { it.entity == entity }
    }

    fun update(deltaTime: Float) {
        play(deltaTime)
    }

    fun play(deltaTime: Float) {
        val iterator = animations.listIterator()
        while (iterator.hasNext()) {
            val animation = iterator.next()
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    drawer.lights.remove(animation.entity to light)
                }
            }
            val remove = !animation.animation.setFrame(deltaTime)
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    drawer.lights.add(animation.entity to light)
                }
            }
            if (remove) {
                iterator.remove()
                if (animation.nextAnimation != null)
                    nextAnimations.add(animation)
            }
        }

        for (animation in nextAnimations) {
            animation.entity.get(Texture::class)!!
                .textures[animation.entity.get(Texture::class)!!.textures.indexOf(animation.animation)] = animation.nextAnimation!!
        }
        nextAnimations.clear()
    }

    fun clear() {
        animations.clear()
    }
}