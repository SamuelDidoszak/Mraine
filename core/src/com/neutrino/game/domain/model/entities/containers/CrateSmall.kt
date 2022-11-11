package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Container
import com.neutrino.game.domain.model.entities.utility.Destructable
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.items.Item

class CrateSmall: Entity(), Destructable, Container {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "CrateSmall"
    override val description = "A small, little crate"
    override var destroyed: Boolean = false
    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(Pair(1, 0.2f))

    override val textureNames: List<String> = listOf("crateSmall")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(textureNames[0])
    }
}