package com.neutrino.game.graphics.drawing.layers

import com.neutrino.game.entities.Entity
import com.neutrino.game.graphics.textures.TextureSprite

internal class DrawableTextureUnsorted(
    entity: Entity,
    texture: TextureSprite
): DrawableTexture(entity, texture) {

    override fun getYSort(): Float {
        return drawPosition.y
    }
}