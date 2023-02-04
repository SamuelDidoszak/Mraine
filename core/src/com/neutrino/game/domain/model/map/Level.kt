package com.neutrino.game.domain.model.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.GameStage
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.Constants
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.domain.use_case.map.MapUseCases
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.graphics.utility.Blurring
import com.neutrino.game.graphics.utility.Pixel
import com.neutrino.game.utility.serialization.ColorSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.random.Random
import kotlin.reflect.KClass

@Serializable
class Level(
    val levelChunkCoords: LevelChunkCoords,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize
): Group() {

    val id: Int = levelChunkCoords.toHash()
    @Transient
    val randomGenerator = Random(Constants.Seed + id)
    @Transient
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    val tagList: List<MapTags> = listOf(MapTags.STARTING_AREA)

    @Transient
    private val mapUsecases = MapUseCases(this)

    val map: Map = Map(
        xMax = sizeX,
        yMax = sizeY,
        map = mapUsecases.getMap()
    )

    @Transient
    private val generateCharacters = GenerateCharacters(this)

    @Transient
    val movementMap: Array<out CharArray> = mapUsecases.getMovementMap()
    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    @Transient
    val characterArray: CharacterArray = generateCharacters.generate()
    // Map of character locations
    @Transient
    val characterMap: List<MutableList<Character?>> = generateCharacters.characterMap

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    /**
     * DEBUG draw overlaying fog of war and FOV
     */
    @Transient
    var drawFovFow: Int = 0

    @Transient
    private val fogOfWarFBO = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax, map.yMax, false)
    @Transient
    private val fovOverlayFBO = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax, map.yMax, false)
    @Transient
    private val blurredFogOfWar = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax * 64, map.yMax * 64, false)
    @Transient
    private val blurredFov = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax * 64, map.yMax * 64, false)
    @Transient
    private val fboBatch = SpriteBatch(128)

    @Serializable(with = ColorSerializer::class)
    private val darkenedColor = Color(0.50f, 0.45f, 0.60f, 1.0f)
    @Serializable(with = ColorSerializer::class)
    private val backgroundColor = Color((21f / 255f) * darkenedColor.r, (21f / 255f) * darkenedColor.g, (23f / 255f) * darkenedColor.b, 1f)
    @Transient
    private lateinit var movementObserver: GlobalDataObserver

    init {
        // Bounds can be provided in the constructor or externally. Their purpose is to place level next to others in gameStage
//        xScreen: Float = 0f,
//        yScreen: Float = 0f

        setBounds(0f, 0f, sizeX * 64f, sizeY * 64f)
        // scene2d name
        setName("Level")

        // initialize fog of war
        fogOfWarFBO.begin()
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        fogOfWarFBO.end()
        fboBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, 100f, 100f)
        fboBatch.disableBlending()
        initializeFogOfWar()

        movementObserver = object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERMOVED
            override fun update(data: Any?): Boolean {
                updateVisibility()
                return true
            }
        }
        GlobalData.registerObserver(movementObserver)
    }

    fun initialize() {
        provideTextures()
        prepareLights()
    }

    /**
     * Fills the level textureList with textures needed by the level
     * Provides the textures for every entity on the map
     * If entity is animated, adds it to the list
     */
    fun provideTextures() {
        // textures for tiles and entities
        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                for (z in 0 until map.map[y][x].size) {
                    if (map.map[y][x][z] is ItemEntity)
                        continue

                    map.map[y][x][z].pickTexture(OnMapPosition(map.map, x, y, z), randomGenerator)
                    if (map.map[y][x][z] is Animated) {
                        (map.map[y][x][z] as Animated).setDefaultAnimation()
                        addAnimatedToList(map.map[y][x][z])
                    }
                }
            }
        }

        // textures for characters
        for (character in characterArray) {
            var exists = false
            val textureSrc = character.textureSrc

            for (atlas in textureList) {
                atlas.textures.forEach {
                    if (it.toString() == textureSrc) {
                        exists = true
                        character.loadTextures(atlas)
                        character.setDefaultAnimation()
                        return@forEach
                    }
                }
            }
            if (!exists) {
                textureList.add(TextureAtlas(character.textureSrc.substring(0, character.textureSrc.lastIndexOf(".")) + ".atlas"))
                character.loadTextures(textureList[textureList.size - 1])
                character.setDefaultAnimation()
            }
        }
    }

    fun allowsCharacter(xPos: Int, yPos: Int): Boolean {
        var allow = true
        for (entity in map.map[yPos][xPos]) {
            if (!entity.allowCharacterOnTop) {
                allow = false
                break
            }
        }
        return allow
    }

    fun allowsCharacterChangesImpassable(xPos: Int, yPos: Int): Boolean {
        var allow = true
        for (entity in map.map[yPos][xPos]) {
            if (!entity.allowCharacterOnTop && entity !is ChangesImpassable) {
                allow = false
                break
            }
        }
        return allow
    }

    /** Returns topmost item on the tile or null */
    fun getTopItem(xPos: Int, yPos: Int): Item? {
        val tile = map.map[yPos][xPos]
        if (tile[tile.size - 1] is ItemEntity)
            return (tile[tile.size - 1] as ItemEntity).item
        else
            return null
    }

    /** Returns topmost entity that has an action associated with it */
    fun getEntityWithAction(xPos: Int, yPos: Int): Entity? {
        for (entity in map.map[yPos][xPos].reversed()) {
            if (entity is Interactable)
                return entity
        }
        return null
    }

    /** Returns topmost entity with provided interaction type */
    fun getEntityWithAction(xPos: Int, yPos: Int, interaction: KClass<Interaction>): Entity? {
        for (entity in map.map[yPos][xPos].reversed()) {
            if (entity is Interactable && entity.interactionList.find { it::class == interaction } != null) {
                return entity
            }
        }
        return null
    }

    @Transient
    private val lights: ArrayList<Pair<Pair<Float, Float>, Color>> = arrayListOf()

    fun prepareLights() {
        var screenX = 0f
        var screenY = height

        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                for (entity in map.map[y][x]) {
                    if (entity !is ItemEntity)
                        addLightFromTexture(entity.texture, screenX, screenY, entity.mirrored)
                }
                screenX += 64f
            }
            screenY -= 64f
            screenX = 0f
        }
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

    private fun initializeFogOfWar() {
        fogOfWarFBO.begin()
        fboBatch.begin()
        Gdx.gl.glColorMask(false, false, false, true)
        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                if (discoveredMap[y][x]) {
                    fboBatch.draw(Constants.TransparentPixel, x.toFloat(), y.toFloat(), 1f, 1f)
                }
            }
        }
        Gdx.gl.glColorMask(true, true, true, true)
        fboBatch.end()
        fogOfWarFBO.end()
    }

    /**
     * Updates the fog of war texture by drawing on top of it in places that were not visited before
     */
    private fun updateFogOfWar() {
        fogOfWarFBO.begin()
        fboBatch.begin()
        Gdx.gl.glColorMask(false, false, false, true)
        for (y in 0 until Player.ai.fov.size) {
            for (x in 0 until Player.ai.fov[0].size) {
                if (Player.ai.fov[y][x] && !discoveredMap[y][x]) {
                    discoveredMap[y][x] = true
                    fboBatch.draw(Constants.TransparentPixel, x.toFloat(), y.toFloat(), 1f, 1f)
                }
            }
        }
        Gdx.gl.glColorMask(true, true, true, true)
        fboBatch.end()
        fogOfWarFBO.end()
    }

    /**
     * Resets the FOV texture
     */
    private fun updateFovTexture() {
        fovOverlayFBO.begin()
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
        fovOverlayFBO.end()
    }

    /**
     * Updates FOV, fog of war and blurs those textures
     */
    private fun updateVisibility() {
        val drawing = parent?.stage?.batch?.isDrawing ?: false
        if (drawing)
            parent.stage.batch.end()

        updateFovTexture()
        updateFogOfWar()
        Blurring.blurTexture(fovOverlayFBO.colorBufferTexture, blurredFov)
        Blurring.blurTexture(fogOfWarFBO.colorBufferTexture, blurredFogOfWar)

        if (drawing)
            parent.stage.batch.begin()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val gameCamera = parent.stage.camera as OrthographicCamera

        var yBottom = ceil((height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 2
        var yTop = floor((height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 64) + 1
        var xLeft: Int = floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)
        var xRight = ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / 64)

        // Make sure that values are in range
        yBottom = if (yBottom <= 0) 0 else if (yBottom > map.map.size) map.map.size else yBottom
        yTop = if (yTop <= 0) 0 else if (yTop > map.map.size) map.map.size else yTop
        xLeft = if (xLeft <= 0) 0 else if (xLeft > map.map[0].size) map.map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > map.map[0].size) map.map[0].size else xRight

        var screenX = xLeft * 64f
        var screenY = height - (yTop * 64f)

        for (y in yTop until yBottom) {
            for (x in xLeft until xRight) {
                for (entity in map.map[y][x]) {
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

                if (characterMap[y][x] != null) {
                    characterMap[y][x]!!.draw(batch, parentAlpha)
                }

                // do not cut out character when it's moving vertically
                if (y != 0 && characterMap[y - 1][x] != null) {
                    if (characterMap[y - 1][x]!!.y < screenY + 64) {
                        val movingChar = characterMap[y - 1][x]!!
                        movingChar.draw(batch, parentAlpha)

                        for (entity in map.map[y][x]) {
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
                if (characterMap[y][x] != null) {
                    val movingChar = characterMap[y][x]!!
                    // down left
                    if (movingChar.x > movingChar.xPos * 64 - 64) {
                        for (entity in map.map[y][x + 1]) {
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
                        for (entity in map.map[y][x - 1]) {
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
            batch?.draw(blurredFov.colorBufferTexture, 0f, 64f)
        }

        /** Reset is unnecessary, because drawLights() flushes the batch and sets its own color **/
//        batch?.flush()
//        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//        batch?.color = Color(1.0f, 1.0f, 1.0f, 1.0f)

        drawLights(batch)

        if (drawFovFow % 3 == 0) {
            batch?.shader = Shaders.defaultShader
            batch?.draw(blurredFogOfWar.colorBufferTexture, 0f, 64f)
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
        if (characterMap[(actor as Character).yPos][(actor as Character).xPos] == actor as Character)
            characterMap[(actor as Character).yPos][(actor as Character).xPos] = null

        removeAnimatedFromList(actor)

        return super.removeActor(actor, unfocus)
    }

    /**
     * Adds actor to the animated list
     */
    private fun addAnimatedToList(actor: Actor) {
        if (actor is Animated)
            (parent?.stage as GameStage?)?.animatedArray?.add(actor)
    }

    /**
     * Adds entity to the animated list
     */
    private fun addAnimatedToList(entity: Entity) {
        if (entity is Animated)
            (parent?.stage as GameStage?)?.animatedArray?.add(entity)
    }

    /**
     * Removes actor from the animated list
     */
    private fun removeAnimatedFromList(actor: Actor) {
        if (actor is Animated)
            (parent?.stage as GameStage?)?.animatedArray?.remove(actor)
    }

    /**
     * Removes entity from the animated list
     */
    private fun removeAnimatedFromList(entity: Entity) {
        if (entity is Animated)
            (parent?.stage as GameStage?)?.animatedArray?.remove(entity)
    }

    fun dispose() {
        GlobalData.unregisterObserver(movementObserver)
        fogOfWarFBO.dispose()
        fovOverlayFBO.dispose()
        blurredFogOfWar.dispose()
        blurredFov.dispose()
        fboBatch.dispose()
    }
}
