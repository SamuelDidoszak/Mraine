package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.graphics.utility.Blurring
import com.neutrino.game.graphics.utility.Pixel
import com.neutrino.game.util.Constants

class LevelDrawer: Group() {

    /**
     * DEBUG draw overlaying fog of war and FOV
     */
    var drawFovFow: Int = 0

    private val fboBatch = SpriteBatch(128)

    private val darkenedColor = Color(0.50f, 0.45f, 0.60f, 1.0f)
    private val backgroundColor = Color((21f / 255f) * darkenedColor.r, (21f / 255f) * darkenedColor.g, (23f / 255f) * darkenedColor.b, 1f)

    private lateinit var currentLevel: Level

    init {
        name = "levelDrawer"
        width = Constants.LevelChunkSize * 64f
        height = Constants.LevelChunkSize * 64f

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERMOVED
            override fun update(data: Any?): Boolean {
                updateVisibility()
                return true
            }
        })
    }

    fun initializeLevel(level: Level) {
        currentLevel = level
        // Bounds can be provided in the constructor or externally. Their purpose is to place level next to others in gameStage
//        xScreen: Float = 0f,
//        yScreen: Float = 0f

        // initialize fog of war
        level.fogOfWarFBO.begin()
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        level.fogOfWarFBO.end()
        fboBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, 100f, 100f)
        fboBatch.disableBlending()
        initializeFogOfWar(level)
        prepareLights(level)
    }

    private val lights: ArrayList<Pair<Pair<Float, Float>, Color>> = arrayListOf()

    // dummy map for later deletion
    private val dummyMap: List<List<MutableList<Entity>>> = List(100) {
        List(100) {
            ArrayList()
        }
    }

    fun prepareLights(level: Level) {
        var screenX = 0f
        var screenY = height

        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (entity in dummyMap[y][x]) {
                    if (entity !is ItemEntity)
                        addLightFromTexture(entity.texture, screenX, screenY, entity.mirrored)
                }
                screenX += 64f
            }
            screenY -= 64f
            screenX = 0f
        }
    }

    fun clearLights() {
        lights.clear()
    }

    private fun addLightFromTexture(texture: TextureAtlas.AtlasRegion, screenX: Float, screenY: Float, mirrored: Boolean = false) {
        val pixelData = Constants.EntityPixelData
        var pixel: Pixel

        for (y in 0 until texture.regionHeight) {
            for (x in 0 until texture.regionWidth) {
                pixel = pixelData.getPixel(texture.regionX + x, texture.regionY + y)
                if (pixel.a() in 100..250)
                    lights.add(Pair(
                        Pair(screenX + if (!mirrored) (x + 1) * 4f - 2f else (texture.regionWidth - (x + 1)) * 4f + 2f, screenY + (texture.regionHeight - y) * 4f - 2f),
                        pixel.color()))
            }
        }
    }

    private fun drawLights(batch: Batch?) {
        batch?.shader = Shaders.lightShader
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        for (light in lights) {
            val intensity = light.second.a * 255f % 25f
            val radius = when (intensity.toInt()) {
                0 -> 8f
                1 -> 16f
                2 -> 24f
                3 -> 32f
                4 -> 48f
                5 -> 64f
                6 -> 128f
                7 -> 192f
                8 -> 256f
                9 -> 320f
                10 -> 384f
                11 -> 448f
                12 -> 512f
                13 -> 640f
                14 -> 768f
                15 -> 896f
                16 -> 1024f
                17 -> 1280f
                18 -> 1536f
                19 -> 1792f
                20 -> 2048f
                21 -> 2560f
                22 -> 3072f
                23 -> 3584f
                24 -> 4096f
                else -> 32f
            }

            batch?.color = light.second
            batch?.draw(
                Constants.WhitePixel,
                light.first.first - radius, light.first.second - radius,
                2 * radius, 2 * radius
            )
        }

        batch?.shader = null
        batch?.color = Color(1f, 1f, 1f, 1f)
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun initializeFogOfWar(level: Level) {
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
    private fun updateVisibility() {
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

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val gameCamera = parent.stage.camera as OrthographicCamera

        var yBottom = MathUtils.ceil((height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 2
        var yTop = MathUtils.floor((height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 1
        var xLeft: Int = MathUtils.floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)
        var xRight = MathUtils.ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)

        // Make sure that values are in range
        yBottom = if (yBottom <= 0) 0 else if (yBottom > currentLevel.map.size) currentLevel.map.size else yBottom
        yTop = if (yTop <= 0) 0 else if (yTop > currentLevel.map.size) currentLevel.map.size else yTop
        xLeft = if (xLeft <= 0) 0 else if (xLeft > currentLevel.map[0].size) currentLevel.map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > currentLevel.map[0].size) currentLevel.map[0].size else xRight

        var screenX = xLeft * 64f
        var screenY = height - (yTop * 64f)

        for (y in yTop until yBottom) {
            for (x in xLeft until xRight) {
                for (entity in dummyMap[y][x]) {
                    batch!!.draw(entity.texture, if (!entity.mirrored) screenX else screenX + entity.texture.regionWidth * 4f, screenY,
                        entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)

                    // Draw a larger part of the texture, so the outline won't cut off
                    for (shader in entity.shaders) {
                        shader?.applyToBatch(batch)
                        batch?.draw(
                            entity.texture.texture,
                            if (!entity.mirrored) screenX - 4f else screenX + entity.texture.regionWidth * 4f + 4f,
                            screenY - 4f,
                            if (!entity.mirrored) entity.texture.regionWidth * 4f + 8f else entity.texture.regionWidth * -4f - 8f,
                            entity.texture.regionHeight * 4f + 8f,
                            entity.texture.regionX - 1,
                            entity.texture.regionY - 1,
                            entity.texture.regionWidth + 2,
                            entity.texture.regionHeight + 2,
                            false, false
                        )
                        shader?.cleanUp(batch)
                    }
                    if (entity.shaders.isNotEmpty())
                        batch?.shader = null
                }
                screenX += 64
            }
            // Render characters
            for (x in xLeft until xRight) {
                // Do not render if not in view
                if (!Player.ai.fov[y][x] && drawFovFow % 3 == 0)
                    continue

                if (currentLevel.characterMap[y][x] != null) {
                    currentLevel.characterMap[y][x]!!.draw(batch, parentAlpha)
                }

                // do not cut out character when it's moving vertically
                if (y != 0 && currentLevel.characterMap[y - 1][x] != null) {
                    if (currentLevel.characterMap[y - 1][x]!!.y < screenY + 64) {
                        val movingChar = currentLevel.characterMap[y - 1][x]!!
                        movingChar.draw(batch, parentAlpha)

                        for (entity in dummyMap[y][x]) {
                            if (entity.texture.regionHeight > 16) {
                                batch!!.draw(entity.texture,
                                    if (!entity.mirrored) movingChar.xPos * 64f else movingChar.xPos * 64f + entity.texture.regionWidth * 4f, screenY,
                                    entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)

                                for (shader in entity.shaders) {
                                    shader?.applyToBatch(batch)
                                    batch!!.draw(entity.texture,
                                        if (!entity.mirrored) movingChar.xPos * 64f else movingChar.xPos * 64f + entity.texture.regionWidth * 4f, screenY,
                                        entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)
                                    shader?.cleanUp(batch)
                                }
                                if (entity.shaders.isNotEmpty())
                                    batch?.shader = null
                            }
                        }
                    }
                }

                // cases for downward diagonal movement
                if (currentLevel.characterMap[y][x] != null) {
                    val movingChar = currentLevel.characterMap[y][x]!!
                    // down left
                    if (movingChar.x > movingChar.xPos * 64 - 64) {
                        for (entity in dummyMap[y][x + 1]) {
                            if (entity.texture.regionHeight > 16) {
                                batch!!.draw(entity.texture,
                                    if (!entity.mirrored) movingChar.xPos * 64f + 64 else movingChar.xPos * 64f + 64 + entity.texture.regionWidth * 4f, screenY,
                                    entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)

                                for (shader in entity.shaders) {
                                    shader?.applyToBatch(batch)
                                    batch!!.draw(entity.texture,
                                        if (!entity.mirrored) movingChar.xPos * 64f + 64 else movingChar.xPos * 64f + 64 + entity.texture.regionWidth * 4f, screenY,
                                        entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)
                                    shader?.cleanUp(batch)
                                }
                                if (entity.shaders.isNotEmpty())
                                    batch?.shader = null
                            }
                        }
                    }
                    // down right
                    if (movingChar.x < movingChar.xPos * 64) {
                        for (entity in dummyMap[y][x - 1]) {
                            if (entity.texture.regionHeight > 16) {
                                batch!!.draw(entity.texture,
                                    if (!entity.mirrored) movingChar.xPos * 64f - 64 else movingChar.xPos * 64f - 64 + entity.texture.regionWidth * 4f, screenY,
                                    entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)

                                for (shader in entity.shaders) {
                                    shader?.applyToBatch(batch)
                                    batch!!.draw(entity.texture,
                                        if (!entity.mirrored) movingChar.xPos * 64f - 64 else movingChar.xPos * 64f - 64 + entity.texture.regionWidth * 4f, screenY,
                                        entity.texture.regionWidth * if (!entity.mirrored) 4f else -4f, entity.texture.regionHeight * 4f)
                                    shader?.cleanUp(batch)
                                }
                                if (entity.shaders.isNotEmpty())
                                    batch?.shader = null
                            }
                        }
                    }
                }
            }
            screenY -= 64
            screenX = xLeft * 64f
        }

        if (drawFovFow % 3 in 0 .. 1) {
            batch?.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO)
            batch?.draw(currentLevel.blurredFov.colorBufferTexture, 0f, 64f)
        }

        /** Reset is unnecessary, because drawLights() flushes the batch and sets its own color **/
//        batch?.flush()
//        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//        batch?.color = Color(1.0f, 1.0f, 1.0f, 1.0f)

        drawLights(batch)

        if (drawFovFow % 3 == 0) {
            batch?.shader = Shaders.defaultShader
            batch?.draw(currentLevel.blurredFogOfWar.colorBufferTexture, 0f, 64f)
            batch?.shader = null
        }
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

    /**
     * Deletes the character from characterMap after fadeOut
     */
    override fun removeActor(actor: Actor?, unfocus: Boolean): Boolean {
        if (currentLevel.characterMap[(actor as Character).yPos][(actor as Character).xPos] == actor as Character)
            currentLevel.characterMap[(actor as Character).yPos][(actor as Character).xPos] = null

        AnimatedActors.remove(actor)

        return super.removeActor(actor, unfocus)
    }
}