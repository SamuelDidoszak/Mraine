package com.neutrino.game.domain.model.items.crystals

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper

class TinyCrystal: Crystal() {
    override val name: String = "Tiny crystal"
    override val description: String = "A tiny piece of crystalized magic"
    override val textureNames: List<String>
        get() = TODO("Not yet implemented")
    override var texture: TextureAtlas.AtlasRegion
        get() = TODO("Not yet implemented")
        set(value) {}
    override val itemTier: Int = 2

    override val possibleEffectList: List<EventWrapper> = listOf(
//        OnOffEvent()
    )
}