package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item

class ClayPot: Entity(), ChangesImpassable, Destructable, Container {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "Clay Pot"
    override val description = "Pot. The one from clay, sadly"
    override var destroyed: Boolean = false
    override var entityHp: Float = 1f
    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(Pair(1, 0.2f))

    override val textureNames: List<String> = listOf(
        "clayPot$1", "clayPot$2", "clayPot$3", "clayPot$4",
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(
            getTextureFromEqualRange(Constants.RandomGenerator.nextFloat() * 100, textures = textureNames.filter { it.startsWith("clayPot$") })!!
        )
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DESTROY(this)
    }
}