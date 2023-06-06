package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.TextureHaver

import kotlin.random.Random

class StandingTorch: Entity(), Animated {
    @Transient
    override val name: String = "Standing torch"
    @Transient
    override var allowOnTop: Boolean = false
    @Transient
    override var allowCharacterOnTop: Boolean = false

    @Transient
    override var animation: Animation<TextureRegion>? = null
    @Transient
    override lateinit var defaultAnimation: Animation<TextureRegion>
    @Transient
    override lateinit var defaultAnimationName: String
    @Transient
    override val textureHaver: TextureHaver = this

    @Transient
    override val textureNames: List<String> = listOf(
        "standingTorch$1#1", "standingTorch$1#2", "standingTorch$1#3",
        "standingTorch$2#1", "standingTorch$2#2", "standingTorch$2#3",
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    @Transient
    override var textureList: List<TextureAtlas.AtlasRegion> = setTextureList(Constants.DefaultEntityTexture)

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