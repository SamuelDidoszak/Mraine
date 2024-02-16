package com.neutrino.game.graphics.drawing

import com.neutrino.game.entities.Entity
import com.neutrino.game.graphics.textures.Light
import com.neutrino.game.graphics.textures.TextureSprite

interface EntityDrawer {

    val animations: Animations
    val lights: ArrayList<Pair<Entity, Light>>

    val map: List<List<MutableList<Entity>>>

    fun addTexture(entity: Entity, texture: TextureSprite)
    fun removeTexture(entity: Entity, texture: TextureSprite)
}