package com.neutrino.game.domain.model.items.scrolls

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class ScrollOfDefence: Item(), ItemType.SCROLL.STAT {
    override val name: String = "Scroll of defence"
    override val description: String = "Makes your skin harder and more resistant to physical attacks"
    override var amount: Int? = 1
    override val causesCooldown: Int = 1

    override val textureNames: List<String> = listOf("scrollOfDefence")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val statName: String = "defence"
    override val power: Float = 15f
    override val repeats: Int = 1
    override val speed: Double = 20.0

    override var goldValueOg: Int = 30
    override var goldValue: Int = goldValueOg
    override var realValue: Int = goldValue
}