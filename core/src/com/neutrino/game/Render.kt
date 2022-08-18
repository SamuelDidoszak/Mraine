package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.Player.textureSrc
import com.neutrino.game.domain.model.map.Level

class Render (
) {
    private var stateTime: Float = 0f

    fun addAnimations() {
        stateTime += Gdx.graphics.deltaTime

        Player.setFrame(stateTime)
    }

    fun loadAdditionalTextures() {
        Player.loadTextures(TextureAtlas(textureSrc.substring(0, textureSrc.lastIndexOf(".")) + ".atlas"))
    }

}