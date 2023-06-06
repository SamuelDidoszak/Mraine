package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition


import kotlin.random.Random

class CandleSingle: Entity() {
    @Transient
    override val name: String = "Candle"
    @Transient
    override var allowOnTop: Boolean = true
    @Transient
    override var allowCharacterOnTop: Boolean = true


    @Transient
    override val textureNames: List<String> = listOf(
        "candleSingle$1", "candleSingle$2", "candleSingle$3", "candleSingle$4", "candleSingle$5"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("candleSingle$") }) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f, randomGenerator)
    }
}