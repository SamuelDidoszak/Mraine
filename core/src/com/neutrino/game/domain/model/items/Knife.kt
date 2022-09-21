package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver

class Knife: Item(), ItemType.WEAPON {
    override val name: String = "Knife"
    override val description: String = "Stabby stab stab"
    override val stackable: Boolean = false


    override var textureSrc: String = "items/knife.png"
    override val textureNames: List<String> = listOf("knife")
    override var texture: TextureAtlas.AtlasRegion = if(textureList.isNotEmpty()) textureList[0] else TextureAtlas.AtlasRegion(
        Constants.DefaultTextures[6][5])
    override val textureHaver: TextureHaver = this
    override var pickedTexture: String? = "knife"
}