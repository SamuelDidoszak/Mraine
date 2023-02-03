package com.neutrino.game.domain.model.entities.lightSources

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class CandleSingle: Entity() {
    override val name: String = "Candle"
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true


    override val textureNames: List<String> = listOf(
        "candleSingle$1", "candleSingle$2", "candleSingle$3", "candleSingle$4", "candleSingle$5"
    )
    override var texture: AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, textures = textureNames.filter { it.startsWith("candleSingle$") }) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f, randomGenerator)
    }
}