package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.MathUtils.ceil
import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.domain.use_case.map.MapUseCases


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
                    batch!!.draw(entity.texture, screenX, screenY, entity.texture.regionWidth * 4f, entity.texture.regionHeight * 4f)
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
                                    movingChar.xPos * 64f,
                                    screenY, entity.texture.regionWidth * 4f, entity.texture.regionHeight * 4f)
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
                                    movingChar.xPos * 64f + 64,
                                    screenY, entity.texture.regionWidth * 4f, entity.texture.regionHeight * 4f)
                            }
                        }
                    }
                    // down right
                    if (movingChar.x < movingChar.xPos * 64) {
                        for (entity in map.map[y][x - 1]) {
                            if (entity.texture.regionHeight > 16) {
                                batch!!.draw(entity.texture,
                                    movingChar.xPos * 64f - 64,
                                    screenY, entity.texture.regionWidth * 4f, entity.texture.regionHeight * 4f)
                            }
                        }
                    }
                }
            }
            screenY -= 64
            screenX = xLeft * 64f
        }
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
