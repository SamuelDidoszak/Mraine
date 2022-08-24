package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.map.Level

class Render (
    var level: Level
) {
    private var stateTime: Float = 0f

    fun addAnimations() {
        stateTime += Gdx.graphics.deltaTime

        for (character in level.characterArray) {
            if (character is Animated)
                character.setFrame(stateTime)
        }
    }

    fun loadAdditionalTextures() {
        for (character in level.characterArray) {
            character.loadTextures(TextureAtlas(character.textureSrc.substring(0, character.textureSrc.lastIndexOf(".")) + ".atlas"))
        }
//        Player.loadTextures(TextureAtlas(textureSrc.substring(0, textureSrc.lastIndexOf(".")) + ".atlas"))
    }

}