package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import kotlin.random.Random

class ClayPotMultiple: Entity(), ChangesImpassable, Destructable, Container {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "Multiple clay pots"
    override val description = "A few clay pots. What a sight to behold"
    override var destroyed: Boolean = false
    override var entityHp: Float = 1f
    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(Pair(1, 0.2f))

    override val textureNames: List<String> = listOf(
        "clayPotMultiple$1", "clayPotMultiple$2", "clayPotMultiple$3"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(
            getTextureFromEqualRange(randomGenerator.nextFloat() * 100, textures = textureNames.filter { it.startsWith("clayPotMultiple$") })!!
        )
        mirror(20f, randomGenerator)
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DESTROY(this)
    }
}