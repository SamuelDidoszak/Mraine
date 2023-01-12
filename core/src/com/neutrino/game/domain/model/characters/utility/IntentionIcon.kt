package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.g2d.TextureRegion

interface IntentionIcon {
    val statusName: String
    val statusTexture: TextureRegion
    val displayTime: Float
}