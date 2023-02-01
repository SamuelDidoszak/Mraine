package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.random.Random

class CandlesMultiple: Entity() {
    override val name: String = "Multiple candles"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false


    override val textureNames: List<String> = listOf(
        "candlesMultiple$1", "candlesMultiple$2", "candlesMultiple$3", "candlesMultiple$4"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("candlesMultiple$") }) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f, randomGenerator)
    }
}