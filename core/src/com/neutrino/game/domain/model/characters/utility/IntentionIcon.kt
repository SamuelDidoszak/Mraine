package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.graphics.textures.TextureSprite

interface IntentionIcon {
    val statusName: String
    val statusTexture: TextureSprite
    val displayTime: Float
}