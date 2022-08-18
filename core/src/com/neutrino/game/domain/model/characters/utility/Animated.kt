package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.AnimationSpeed
import com.neutrino.game.domain.model.characters.Player.animation
import com.neutrino.game.domain.model.characters.Player.textureHaver
import com.neutrino.game.domain.model.entities.utility.TextureHaver

interface Animated {
    var animation: Animation<TextureRegion>?
    val textureHaver: TextureHaver

    fun setAnimation(name: String) {
        val regex = "$name#[0-9]+".toRegex()
        val nameList = textureHaver.textureNames.filter {
            regex.matches(it)
        }
        val frames = com.badlogic.gdx.utils.Array<TextureRegion>(nameList.size)

        for(element in nameList) {
            frames.add(textureHaver.getTexture(element))
        }
        animation = Animation<TextureRegion>(AnimationSpeed, frames)
        // initialize Actor dimensions
        if (nameList.isNotEmpty())
            textureHaver.setTexture(nameList[0])
    }

    fun setFrame(stateTime: Float) {
        textureHaver.texture = TextureAtlas.AtlasRegion(animation?.getKeyFrame(stateTime, true))
    }
}