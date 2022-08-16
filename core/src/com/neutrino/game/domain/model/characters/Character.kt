package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.SpriteHandler

abstract class Character (
    val id: Int,
    val name: String,
    val description: String,
//    val spriteHandler: SpriteHandler
) {

//    init {
//        animation = spriteHandler.getAnimation(0)
//    }
}