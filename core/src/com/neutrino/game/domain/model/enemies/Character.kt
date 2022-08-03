package com.neutrino.game.domain.model.enemies

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.SpriteHandler

class Character (
    val id: Int,
    val name: Int,
    val description: String,
    val spriteHandler: SpriteHandler
) {
    val animation: Animation<TextureRegion>

    init {
        animation = spriteHandler.getAnimation(0)
    }
}