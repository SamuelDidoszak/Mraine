package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.random.Random

@Serializable
class StandingTorch: Entity(), Animated {
    override val name: String = "Standing torch"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false

    @Transient
    override var animation: Animation<TextureRegion>? = null
    @Transient
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override lateinit var defaultAnimationName: String
    override val textureHaver: TextureHaver = this

    override val textureNames: List<String> = listOf(
        "standingTorch$1#1", "standingTorch$1#2", "standingTorch$1#3",
        "standingTorch$2#1", "standingTorch$2#2", "standingTorch$2#3",
    )
    override var texture: AtlasRegion = setTexture()
    override var textureList: List<AtlasRegion> = setTextureList(Constants.DefaultEntityTexture)

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        val textureName =
            getTextureFromEqualRange(randVal, 0f, 65f, listOf("standingTorch$1#1")) ?:
            getTextureFromEqualRange(randVal, 65f, textures = listOf("standingTorch$2#1")) ?:
            textureNames[0]
        texture = getTexture(textureName)
        mirror(20f, randomGenerator)
        defaultAnimationName = textureName.substringBefore('#')
    }
}