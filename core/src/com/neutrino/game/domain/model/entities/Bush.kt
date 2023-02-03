package com.neutrino.game.domain.model.entities

import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

class Bush: Entity() {
    override val name = "Bush"
    override val description = "A little bush"
    override var allowOnTop = true
    override var allowCharacterOnTop = true

    override val textureNames: List<String> = listOf()
    override var texture: AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {

    }
}