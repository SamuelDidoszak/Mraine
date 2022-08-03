package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Render (
    private val batch: SpriteBatch
) {
    private var stateTime: Float = 0f

    val handler = SpriteHandler(Texture("sprites/rat.png"))
    val ratAnimation = handler.getAnimation(0)
    val ratAnimation2 = handler.getAnimation(1)
    val ratAnimation3 = handler.getAnimation(2)
    val ratAnimation4 = handler.getAnimation(3)
    val ratAnimation5 = handler.getAnimation(4)

    val envHandler = SpriteHandler(Texture("environment/entities.png"))

    fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stateTime += Gdx.graphics.deltaTime

        batch.draw(ratAnimation.getKeyFrame(stateTime, true), 100f, 100f)
        batch.draw(ratAnimation2.getKeyFrame(stateTime, true), 132f, 100f)
        batch.draw(ratAnimation3.getKeyFrame(stateTime, true), 164f, 100f)
        batch.draw(ratAnimation4.getKeyFrame(stateTime, true), 100f, 132f)
        batch.draw(ratAnimation5.getKeyFrame(stateTime, true), 100f, 164f)

        for (i in 0 until 48) {
            batch.draw(envHandler.getTexture(i), i*16f, 300f)
        }
    }

}