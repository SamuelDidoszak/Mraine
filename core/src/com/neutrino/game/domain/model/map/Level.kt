package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.neutrino.AnimatedActors
import com.neutrino.game.Constants
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.domain.use_case.map.MapUseCases
import kotlin.random.Random
import kotlin.reflect.KClass

class Level(
    val levelChunkCoords: LevelChunkCoords,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize
) {

    val id: Int = levelChunkCoords.toHash()
    @Transient
    val randomGenerator = Random(Constants.Seed + id)
    @Transient
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    val tagList: List<MapTags> = listOf(MapTags.STARTING_AREA)

    @Transient
    private val mapUsecases = MapUseCases(this)

    @Transient
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

    @Transient
    val fogOfWarFBO = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax, map.yMax, false)
    @Transient
    val fovOverlayFBO = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax, map.yMax, false)
    @Transient
    val blurredFogOfWar = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax * 64, map.yMax * 64, false)
    @Transient
    val blurredFov = FrameBuffer(Pixmap.Format.RGBA8888, map.xMax * 64, map.yMax * 64, false)

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
                        AnimatedActors.add(map.map[y][x][z])
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

    fun dispose() {
        fogOfWarFBO.dispose()
        fovOverlayFBO.dispose()
        blurredFogOfWar.dispose()
        blurredFov.dispose()
    }
}
