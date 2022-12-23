package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class StandingTorch: Entity() {
    override val name: String = "Standing torch"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false


    override val textureNames: List<String> = listOf(
        "standingTorch$1", "standingTorch$2"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = Constants.RandomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, 0f, 65f, listOf("standingTorch$1")) ?:
            getTextureFromEqualRange(randVal, 65f, textures = listOf("standingTorch$2")) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f)
    }
}