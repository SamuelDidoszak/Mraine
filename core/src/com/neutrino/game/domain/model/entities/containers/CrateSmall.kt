package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item

import kotlin.random.Random

class CrateSmall: Entity(), ChangesImpassable, Destructable, Container {
    @Transient
    override val name = "CrateSmall"
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    @Transient
    override val description = "A small, little crate"
    override var destroyed: Boolean = false
    override var entityHp: Float = 1f
    override val itemList: MutableList<Item> = ArrayList()
    @Transient
    override val itemTiers: List<Pair<Int, Float>> = listOf(Pair(1, 0.2f))

    @Transient
    override val textureNames: List<String> = listOf("crateSmall")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
        mirror(20f, randomGenerator)
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DESTROY(this)
    }
}