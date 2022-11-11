package com.neutrino.game.domain.model.entities.containers

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Container
import com.neutrino.game.domain.model.entities.utility.Destructable
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.items.Item

class CrateBigger: Entity(), Destructable, Container {
    override var allowOnTop = false
    override var allowCharacterOnTop = false
    override val name = "CrateBigger"
    override val description = "A bigger crate. Surprisingly, it has more volume than a smaller one"
    override var destroyed: Boolean = false
    override val itemList: MutableList<Item> = ArrayList()
    override val itemTiers: List<Pair<Int, Float>> = listOf(
        Pair(1, 0.35f),
        Pair(2, 0.1f)
    )

    override val textureNames: List<String> = listOf("crateBiggerDark")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(textureNames[0])
    }
}