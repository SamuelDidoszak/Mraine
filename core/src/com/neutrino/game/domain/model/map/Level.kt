package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.turn.CharacterArray
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.domain.use_case.map.GenerateCharacters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.attributes.Item
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.LevelChunkSize
import com.neutrino.game.utility.serialization.HeaderSerializable
import kotlin.random.Random
import kotlin.reflect.KClass

class Level(
    @Transient
    val levelChunkCoords: LevelChunkCoords,
    @Transient
    val description: String,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize
): HeaderSerializable {

    constructor(kryo: Kryo?, input: Input?): this(
        kryo?.readClassAndObject(input) as LevelChunkCoords,
        kryo.readClassAndObject(input) as String
    )

    override fun serializeHeader(kryo: Kryo?, output: Output?) {
        kryo?.writeClassAndObject(output, levelChunkCoords)
        kryo?.writeClassAndObject(output, description)
    }

    override fun readAfter(kryo: Kryo?, input: Input?) {
        movementMap = createMovementMap()
        val generateCharacters = GenerateCharacters(this)
        characterArray = generateCharacters.generate()
        characterMap = createCharacterMap()
    }

    @Transient
    val id: Int = levelChunkCoords.toHash()
    @Transient
    val randomGenerator = Random(Constants.Seed + id)
    @Transient
    val textureList: ArrayList<TextureAtlas> = ArrayList()

    // listOf(MapTags.STARTING_AREA)
    var tagList: List<MapTags> = listOf()

    lateinit var map: List<List<MutableList<Entity>>>

    @Transient
    lateinit var movementMap: Array<out CharArray>

    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    @Transient
    lateinit var characterArray: CharacterArray
    // Map of character locations
    @Transient
    lateinit var characterMap: List<MutableList<Character?>>

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    @Transient
    val fogOfWarFBO = FrameBuffer(Pixmap.Format.RGBA8888, sizeX, sizeY, false)
    @Transient
    val fovOverlayFBO = FrameBuffer(Pixmap.Format.RGBA8888, sizeX, sizeY, false)
    @Transient
    val blurredFogOfWar = FrameBuffer(Pixmap.Format.RGBA8888, sizeX * 64, sizeY * 64, false)
    @Transient
    val blurredFov = FrameBuffer(Pixmap.Format.RGBA8888, sizeX * 64, sizeY * 64, false)

    fun createMovementMap(): Array<out CharArray> {
        val movementMap: Array<out CharArray> = Array(sizeY) {CharArray(sizeX) {'.'} }
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                for (entity in map[y][x]) {
                    if (!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                        movementMap[x][y] = '#'
                        break
                    }
                }
            }
        }
        return movementMap
    }

    fun createCharacterMap(): List<MutableList<Character?>> {
        val characterMap = List(sizeY) {
            MutableList<Character?>(sizeX) {null}
        }

        characterArray.forEach {
            characterMap[it.yPos][it.xPos] = it
        }
        return characterMap
    }

    fun allowsCharacter(xPos: Int, yPos: Int): Boolean {
        var allow = true
        for (entity in map[yPos][xPos]) {
            if (!entity.get(MapParams::class)!!.allowCharacterOnTop) {
                allow = false
                break
            }
        }
        return allow
    }

    fun allowsCharacterChangesImpassable(xPos: Int, yPos: Int): Boolean {
        var allow = true
        for (entity in map[yPos][xPos]) {
            if (!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                allow = false
                break
            }
        }
        return allow
    }

    /** Returns topmost item on the tile or null */
    fun getTopItem(xPos: Int, yPos: Int): Item? {
        val tile = map[yPos][xPos]
        if (tile[tile.size - 1] has Item::class)
            return tile[tile.size - 1] get Item::class
        else
            return null
    }

    /** Returns topmost entity that has an action associated with it */
    fun getEntityWithAction(xPos: Int, yPos: Int): Entity? {
        for (entity in map[yPos][xPos].reversed()) {
            if (entity has Interaction::class)
                return entity
        }
        return null
    }

    /** Returns topmost entity with provided interaction type */
    fun getEntityWithAction(xPos: Int, yPos: Int, interaction: KClass<InteractionType>): Entity? {
        for (entity in map[yPos][xPos].reversed()) {
            if (entity.get(Interaction::class)?.interactionList?.find { it::class == interaction } != null) {
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
