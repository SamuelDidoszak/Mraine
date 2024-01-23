package com.neutrino.game.graphics.drawing

import com.neutrino.game.entities.Entity
import com.neutrino.game.graphics.textures.TextureSprite

internal class LayeredTextureUnsorted(
    entity: Entity,
    texture: TextureSprite
): LayeredTexture(entity, texture) {

    override fun getYSort(): Float {
        return getYPos()
    }
}