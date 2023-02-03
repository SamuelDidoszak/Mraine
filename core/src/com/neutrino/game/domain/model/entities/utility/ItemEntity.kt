package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class ItemEntity(val item: Item): Entity(), Interactable {
    override val name: String = item.name
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true

    override val textureNames: List<String> = item.textureNames
    override var texture: AtlasRegion = item.texture
    // Unnecessarily required for entity

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) { }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.ITEM()
    }

    override var shaders: ArrayList<ShaderParametered?> = arrayListOf(OutlineShader(OutlineShader.OUTLINE_BLACK, 2f, texture))
}