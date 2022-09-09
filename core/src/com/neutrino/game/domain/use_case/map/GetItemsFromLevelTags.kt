package com.neutrino.game.domain.use_case.map

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.map.Level

/**
 * Returns a list of textures that can appear on a particular level, from its tags
 */
class GetItemsFromLevelTags(
    private val level: Level
) {
    operator fun invoke(): ArrayList<TextureAtlas> {
        val listOfTextures: ArrayList<TextureAtlas> = ArrayList()
        listOfTextures.add(TextureAtlas("items/gold.atlas"))
        return listOfTextures
    }
}