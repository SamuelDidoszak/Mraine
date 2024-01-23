package com.neutrino.game.graphics.textures

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.Array

class AnimatedTextureSprite(
    textureList: Array<TextureAtlas.AtlasRegion>,
    private val looping: Boolean = true,
    val animationSpeed: Float = 0.16666667f,
    x: Float = 0f,
    y: Float = 0f,
    z: Int = 1
): TextureSprite(textureList[0], x, y, z) {

    constructor(
        textureList: Array<TextureAtlas.AtlasRegion>,
        looping: Boolean = true,
        animationSpeed: Float = 0.16666667f,
        lightSources: LightSources?,
        x: Float = 0f,
        y: Float = 0f,
        z: Int = 1
    ): this(textureList, looping, animationSpeed, x, y, z) {
        this.lights = lightSources
    }

    private val animation = Animation<TextureAtlas.AtlasRegion>(
        animationSpeed, textureList,
        if (looping) Animation.PlayMode.LOOP else Animation.PlayMode.NORMAL
    )

    // Temporary
    fun getTextureList(): Array<TextureAtlas.AtlasRegion> {
        val array = Array<TextureAtlas.AtlasRegion>()
        animation.keyFrames.forEach {
            array.add(it)
        }
        return array
    }

    // Temporary
    fun getLooping(): Boolean {
        return looping
    }

    private var stateTime = 0f

    /**
     * Sets the new frame of animation.
     * @return false if animation is not looping and it finished
     */
    fun setFrame(deltaTime: Float): Boolean {
        stateTime += deltaTime
        if (!looping && animation.isAnimationFinished(stateTime))
            return false
        texture = animation.getKeyFrame(stateTime) as TextureAtlas.AtlasRegion
        return true
    }

    fun getCurrentLights(): ArrayList<Light>? {
        return lights?.getLights(animation.getKeyFrameIndex(stateTime))
    }
}