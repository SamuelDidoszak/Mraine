package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity

object AnimatedActors {

    var stateTime: Float = 0f
    private val animatedArray: ArrayList<Animated> = ArrayList()

    fun animateAll() {
        stateTime += Gdx.graphics.deltaTime
        for (animated in animatedArray)
            animated.setFrame(stateTime)
    }

    fun addAll(list: List<Animated>) {
        animatedArray.addAll(list)
    }

    fun clear() {
        animatedArray.clear()
    }

    /**
     * Adds actor to the animated list
     */
    fun add(actor: Actor) {
        if (actor is Animated)
            animatedArray.add(actor)
    }

    /**
     * Adds entity to the animated list
     */
    fun add(entity: Entity) {
        if (entity is Animated)
            animatedArray.add(entity)
    }

    /**
     * Removes actor from the animated list
     */
    fun remove(actor: Actor) {
        if (actor is Animated)
            animatedArray.remove(actor)
    }

    /**
     * Removes entity from the animated list
     */
    fun remove(entity: Entity) {
        if (entity is Animated)
            animatedArray.remove(entity)
    }
}