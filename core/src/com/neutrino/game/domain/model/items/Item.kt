package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.TextureHaver

abstract class Item: TextureHaver {
    abstract val name: String
    abstract val description: String
    open val subType: ItemSubType? = null
    abstract val stackable: Boolean
    open var amount: Int? = if (!stackable) null else 0

    override var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    abstract val textureHaver: TextureHaver

    open val pickedTexture: String? = null
}