package com.neutrino.game.graphics.utility

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.neutrino.game.Constants

object Blurring {
    private val blurBatch = SpriteBatch(2)
    private val rescaledTexture1 = FrameBuffer(Pixmap.Format.RGBA8888, Constants.LevelChunkSize * 2, Constants.LevelChunkSize * 2, false)
    private val rescaledTexture2 = FrameBuffer(Pixmap.Format.RGBA8888, Constants.LevelChunkSize * 4, Constants.LevelChunkSize * 4, false)
    private val rescaledTexture3 = FrameBuffer(Pixmap.Format.RGBA8888, Constants.LevelChunkSize * 8, Constants.LevelChunkSize * 8, false)
    private val fullSizeTexture = FrameBuffer(Pixmap.Format.RGBA8888, Constants.LevelChunkSize * 64, Constants.LevelChunkSize * 64, false)
    
    init {
        rescaledTexture1.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        rescaledTexture2.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        rescaledTexture3.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        fullSizeTexture.colorBufferTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        blurBatch.disableBlending()
    }

    /**
     * Returns blurred texture by downsampling it on GPU
     * Convenience method which resets current batch before blurring
     */
    fun blurTexture(batch: Batch?, texture: Texture): Texture {
        batch?.end()
        blurTexture(texture)
        batch?.begin()
        return fullSizeTexture.colorBufferTexture
    }

    /**
     * Returns blurred texture by downsampling it on GPU
     */
    fun blurTexture(texture: Texture): Texture {
        blurTexture(texture, fullSizeTexture)
        return fullSizeTexture.colorBufferTexture
    }

    /**
     * Draws blurred texture into texture provided
     */
    fun blurTexture(texture: Texture, blurInto: FrameBuffer): Texture {
        rescaledTexture1.begin()
        blurBatch.begin()
        blurBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, rescaledTexture1.width.toFloat(), rescaledTexture1.height.toFloat())
        blurBatch.draw(texture, 0f, rescaledTexture1.height.toFloat(), rescaledTexture1.width.toFloat(), -1 * rescaledTexture1.height.toFloat())
        blurBatch.end()
        rescaledTexture1.end()

        rescaledTexture2.begin()
        blurBatch.begin()
        blurBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, rescaledTexture2.width.toFloat(), rescaledTexture2.height.toFloat())
        blurBatch.draw(rescaledTexture1.colorBufferTexture, 0f, rescaledTexture2.height.toFloat(), rescaledTexture2.width.toFloat(), -1 * rescaledTexture2.height.toFloat())
        blurBatch.end()
        rescaledTexture2.end()

//        rescaledTexture3.begin()
//        blurBatch.begin()
//        blurBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, rescaledTexture3.width.toFloat(), rescaledTexture3.height.toFloat())
//        blurBatch.draw(rescaledTexture3.colorBufferTexture, 0f, rescaledTexture3.height.toFloat(), rescaledTexture3.width.toFloat(), -1 * rescaledTexture3.height.toFloat())
//        blurBatch.end()
//        rescaledTexture3.end()

        blurInto.begin()
        blurBatch.begin()
        blurBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, blurInto.width.toFloat(), blurInto.height.toFloat())
        blurBatch.draw(rescaledTexture2.colorBufferTexture, 0f, blurInto.height.toFloat(), blurInto.width.toFloat(), -1 * blurInto.height.toFloat())
        blurBatch.end()
        blurInto.end()

        return blurInto.colorBufferTexture
    }
}