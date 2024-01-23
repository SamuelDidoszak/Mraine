package com.neutrino.game.graphics.drawing

import com.neutrino.game.entities.Entity
import com.neutrino.game.map.attributes.OnMapPosition
import com.neutrino.game.entities.shared.attributes.Texture

class Animations {
    private val animations = ArrayList<AnimationData>(10)

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
            val levelLights = animation.entity.get(OnMapPosition::class)!!.level.lights
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    levelLights.remove(animation.entity to light)
                }
            }
            val remove = !animation.animation.setFrame(deltaTime)
            if (animation.animation.getCurrentLights() != null) {
                for (light in animation.animation.getCurrentLights()!!) {
                    levelLights.add(animation.entity to light)
                }
            }
            if (remove) {
                iterator.remove()
                if (animation.nextAnimation == null)
                    continue
                animation.entity.get(Texture::class)!!
                    .textures[animation.entity.get(Texture::class)!!.textures.indexOf(animation.animation)] = animation.nextAnimation
                iterator.add(AnimationData(animation.nextAnimation, animation.entity, null))
            }
        }
    }

    fun clear() {
        animations.clear()
    }
}