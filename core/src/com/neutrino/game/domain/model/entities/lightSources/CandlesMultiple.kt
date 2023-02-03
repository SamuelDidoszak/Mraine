package com.neutrino.game.domain.model.entities.lightSources

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class CandlesMultiple: Entity() {
    override val name: String = "Multiple candles"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false


    override val textureNames: List<String> = listOf(
        "candlesMultiple$1", "candlesMultiple$2", "candlesMultiple$3", "candlesMultiple$4"
    )
    override var texture: AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("candlesMultiple$") }) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f, randomGenerator)
    }
}