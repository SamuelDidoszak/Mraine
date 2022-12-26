package com.neutrino.game.domain.model.map

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.Constants
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Fov
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.domain.use_case.map.MapUseCases
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.graphics.utility.Pixel
import squidpony.squidmath.Coord


class Level(
    name: String,
    val xPosition: Int,
    val yPosition: Int,
    val zPosition: Int,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize,
    val xScreen: Float = 0f,
    val yScreen: Float = 0f,
    val difficulty: Float,
    movementMap: Array<out CharArray>? = null,
    characterArray: CharacterArray? = null
): Group() {
    val id: Int = "$xPosition-$yPosition-$zPosition".hashCode()
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    val tagList: List<MapTags> = listOf(MapTags.STARTING_AREA)

    private val mapUsecases = MapUseCases(this)

    val map: Map = Map(id,
        name = "$name $zPosition",
        xMax = sizeX,
        yMax = sizeY,
        map = mapUsecases.getMap()
    )

    val movementMap: Array<out CharArray> = movementMap?:mapUsecases.getMovementMap()
    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    val characterArray: CharacterArray = characterArray?:GenerateCharacters(this)()
    // Map of character locations
    val characterMap: List<MutableList<Character?>> = List(sizeY) {
        ArrayList<Character?>(sizeX)
    }

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    private val fogOfWarFBO = FrameBuffer(Pixmap.Format.RGBA8888, 6400, 6400, false)
    private val fogOfWarRegion = TextureRegion(fogOfWarFBO.colorBufferTexture)
    val fboBatch = SpriteBatch(128)

    init {
        setBounds(xScreen, yScreen, sizeX * 64f, sizeY * 64f)
        // scene2d name
        setName("Level")

        // initialize characterMap
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                characterMap[y].add(this.characterArray.find { it.yPos == y && it.xPos == x })
            }
        }

//        // initialize fog of war
        fogOfWarFBO.begin()
        Gdx.gl.glClearColor(21f / 255f, 21f / 255f, 23f / 255f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        fogOfWarFBO.end()
        fboBatch.projectionMatrix = Matrix4().setToOrtho2D(0f, 0f, 6400f, 6400f)
        fboBatch.disableBlending()
    }

    /**
     * Fills the level textureList with textures needed by the level
     * Provides the textures for every entity on the map
     */
    fun provideTextures() {
        // textures for tiles and entities
        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                for (z in 0 until map.map[y][x].size) {
                    if (map.map[y][x][z] is ItemEntity)
                        continue

                    map.map[y][x][z].pickTexture(OnMapPosition(map.map, x, y, z))
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

    private fun updateFogOfWar(batch: Batch?, viewedTilesList: List<Coord>) {
        batch?.end()
        fogOfWarFBO.begin()
        fboBatch?.begin()
        for (tile in viewedTilesList) {
            if (discoveredMap[tile.y][tile.x])
                continue
            discoveredMap[tile.y][tile.x] = true
            fboBatch?.draw(Constants.FogOfWarTexture, tile.x * 64f, tile.y * 64f, 64f, 64f)
        }
        fboBatch?.end()
        fogOfWarFBO.end()
        batch?.begin()
    }
    val fov = Fov(map)

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
                    }
                    if (entity.shaders.isNotEmpty())
                        batch?.shader = null
                }
                screenX += 64
            }
            // Render characters
            for (x in xLeft until xRight) {
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

        drawLights(batch)

        fov.cast(Player.xPos, Player.yPos)
        updateFogOfWar(batch, fov.coordList)
        batch?.draw(fogOfWarFBO.colorBufferTexture, 0f, 64f)
//        // draw the children after level layout
//        super.draw(batch, parentAlpha)
    }

    /**
     * Deletes the character from characterMap after fadeOut
     */
    override fun removeActor(actor: Actor?, unfocus: Boolean): Boolean {
        if (characterMap[(actor as Character).yPos][(actor as Character).xPos] == actor as Character)
            characterMap[(actor as Character).yPos][(actor as Character).xPos] = null
        return super.removeActor(actor, unfocus)
    }
}
