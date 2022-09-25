package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver

abstract class Item: TextureHaver {
    abstract val name: String
    abstract val description: String
    open val subType: ItemSubType? = null
    abstract val stackable: Boolean
    open var amount: Int? = null
    /** Specifies if the item causes cooldown. -1 means no, 0 means player and 1 means every use type */
    open val causesCooldown: Int = -1

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return Constants.DefaultItemTexture.findRegion(name)
    }
}