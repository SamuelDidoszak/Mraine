package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants.AnimationSpeed
import com.neutrino.game.domain.model.entities.utility.TextureHaver

interface Animated {
    var animation: Animation<TextureRegion>?
    /** set it by calling the setDefaultAnimation method */
    var defaultAnimation: Animation<TextureRegion>
    val defaultAnimationName: String
    val textureHaver: TextureHaver

    var textureList: List<TextureAtlas.AtlasRegion>

    fun setAnimation(name: String, looping: Boolean = true) {
        val regex = "${name.replace("$", "\\$")}#[0-9]+".toRegex()
        val nameList = textureHaver.textureNames.filter {
            regex.matches(it)
        }
        val frames = com.badlogic.gdx.utils.Array<TextureRegion>(nameList.size)

        for(element in nameList) {
            frames.add(textureHaver.getTexture(element))
        }
        animation = Animation<TextureRegion>(AnimationSpeed, frames,
            if (looping) Animation.PlayMode.LOOP else Animation.PlayMode.NORMAL)
        // initialize Actor dimensions
        if (nameList.isNotEmpty())
            textureHaver.setTexture(nameList[0])
    }

    fun setFrame(stateTime: Float) {
        if (!animation!!.isAnimationFinished(stateTime))
            textureHaver.texture = animation!!.getKeyFrame(stateTime) as TextureAtlas.AtlasRegion
        else
            textureHaver.texture = defaultAnimation.getKeyFrame(stateTime) as TextureAtlas.AtlasRegion
    }

    fun setDefaultAnimation() {
        setAnimation(defaultAnimationName, true)
        defaultAnimation = animation!!
        textureHaver.texture = TextureAtlas.AtlasRegion(defaultAnimation.getKeyFrame(0f))
    }

    fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureHaver.textureNames) {
                add(atlas.findRegion(name))
            } }
    }

    fun setTextureList(atlas: TextureAtlas): List<TextureAtlas.AtlasRegion> {
        return buildList {
            for (name in textureHaver.textureNames) {
                add(atlas.findRegion(name))
            } }
    }
}