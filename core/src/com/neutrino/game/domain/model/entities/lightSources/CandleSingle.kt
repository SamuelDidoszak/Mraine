package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.random.Random

class CandleSingle: Entity() {
    override val name: String = "Candle"
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true


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