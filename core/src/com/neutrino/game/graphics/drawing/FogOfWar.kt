package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.Turn.currentLevel
import com.neutrino.game.graphics.utility.Blurring
import com.neutrino.game.util.Constants

class FogOfWar {

    /**
     * DEBUG draw overlaying fog of war and FOV
     */
    var drawFovFow: Int = 0

    private val fboBatch = SpriteBatch(128)
    private val darkenedColor = Color(0.50f, 0.45f, 0.60f, 1.0f)
    private val backgroundColor = Color((21f / 255f) * darkenedColor.r, (21f / 255f) * darkenedColor.g, (23f / 255f) * darkenedColor.b, 1f)

    fun initializeFogOfWar(level: Level) {
        level.fogOfWarFBO.begin()
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        level.fogOfWarFBO.end()
        fboBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, 100f, 100f)
        fboBatch.disableBlending()

        level.fogOfWarFBO.begin()
        fboBatch.begin()
        Gdx.gl.glColorMask(false, false, false, true)
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                if (level.discoveredMap[y][x]) {
                    fboBatch.draw(Constants.TransparentPixel, x.toFloat(), y.toFloat(), 1f, 1f)
                }
            }
        }
        Gdx.gl.glColorMask(true, true, true, true)
        fboBatch.end()
        level.fogOfWarFBO.end()
    }

    /**
     * Updates the fog of war texture by drawing on top of it in places that were not visited before
     */
    private fun updateFogOfWar(level: Level) {
        level.fogOfWarFBO.begin()
        fboBatch.begin()
        Gdx.gl.glColorMask(false, false, false, true)
        for (y in 0 until Player.ai.fov.size) {
            for (x in 0 until Player.ai.fov[0].size) {
                if (Player.ai.fov[y][x] && !level.discoveredMap[y][x]) {
                    level.discoveredMap[y][x] = true
                    fboBatch.draw(Constants.TransparentPixel, x.toFloat(), y.toFloat(), 1f, 1f)
                }
            }
        }
        Gdx.gl.glColorMask(true, true, true, true)
        fboBatch.end()
        level.fogOfWarFBO.end()
    }

    /**
     * Resets the FOV texture
     */
    private fun updateFovTexture(level: Level) {
        level.fovOverlayFBO.begin()
        fboBatch.begin()

        Gdx.gl.glClearColor(darkenedColor.r, darkenedColor.g, darkenedColor.b, darkenedColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        for (y in 0 until Player.ai.fov.size) {
            for (x in 0 until Player.ai.fov[0].size) {
                if (Player.ai.fov[y][x])
                    fboBatch.draw(Constants.WhitePixel, x.toFloat(), y.toFloat(), 1f, 1f)
            }
        }

        fboBatch.end()
        level.fovOverlayFBO.end()
    }

    /**
     * Updates FOV, fog of war and blurs those textures
     */
    fun updateVisibility() {
//        val drawing = parent?.stage?.batch?.isDrawing ?: false
//        if (drawing)
//            parent.stage.batch.end()

        updateFovTexture(currentLevel)
        updateFogOfWar(currentLevel)
        Blurring.blurTexture(currentLevel.fovOverlayFBO.colorBufferTexture, currentLevel.blurredFov)
        Blurring.blurTexture(currentLevel.fogOfWarFBO.colorBufferTexture, currentLevel.blurredFogOfWar)

//        if (drawing)
//            parent.stage.batch.begin()
    }


    /**
     * Overlays the batch with a provided color by multiplying destination with itself
     */
    private fun drawOverlayingColor(batch: Batch?, color: Color) {
        batch?.color = Color(color.r, color.g, color.b, color.a)
        batch?.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO)
        batch?.draw(Constants.WhitePixel, 0f, 64f, 6400f, 6400f)

        // Reset batch settings
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch?.color = Color(1.0f, 1.0f, 1.0f, 1.0f)
    }
}