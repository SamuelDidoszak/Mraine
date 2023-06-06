package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.Serialize

import kotlin.random.Random

@Serialize
class DungeonStairsDown: Entity() {
    @Transient
    override var allowOnTop = false
    @Transient
    override var allowCharacterOnTop = true
    @Transient
    override val name = "Dungeon stairs down"
    @Transient
    override val description = "You can go deeper"

    @Transient
    override val textureNames: List<String> = listOf("dungeonStairsDown")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }
}