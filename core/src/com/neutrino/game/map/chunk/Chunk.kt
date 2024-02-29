package com.neutrino.game.map.chunk

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.map.generation.MapTag
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.LevelChunkSize
import com.neutrino.game.utility.serialization.HeaderSerializable
import kotlin.random.Random
import kotlin.reflect.KClass

class Chunk(
    @Transient
    val chunkCoords: ChunkCoords,
): HeaderSerializable {

    constructor(kryo: Kryo?, input: Input?): this(
        kryo?.readClassAndObject(input) as ChunkCoords
    )

    override fun serializeHeader(kryo: Kryo?, output: Output?) {
        kryo?.writeClassAndObject(output, chunkCoords)
    }

    override fun readAfter(kryo: Kryo?, input: Input?) {
        afterMapGeneration()
        // TODO ECS Generation
//        val characterGenerator = CharacterGenerator(GenerationParams(MapTagInterpretation(listOf()), randomGenerator, this, map))
//        characterArray = characterGenerator.generate()
//        characterMap = createCharacterMap()
        // OLD
//        val generateCharacters = GenerateCharacters(this)
//        characterArray = generateCharacters.generate()
//        characterMap = createCharacterMap()
    }

    val sizeX: Int
        get() = LevelChunkSize
    val sizeY: Int
        get() = LevelChunkSize

    @Transient
    val id: Int = chunkCoords.toHash()
    @Transient
    val randomGenerator = Random(Constants.Seed + id)
    var tagList: List<MapTag> = listOf()

    private var isMapSet = false
    val map: List<List<EntityList>> = List(sizeY) { List(sizeX) { EntityList(::onEntityChanged) } }

    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    lateinit var characterArray: CharacterArray
    // Map of character locations
    @Transient
    lateinit var characterMap: List<MutableList<Entity?>>

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    fun afterMapGeneration() {
        isMapSet = true
    }

    private fun onEntityChanged(entity: Entity, added: Boolean) {
        if (!isMapSet)
            return
        if (!added)
            entity.get(Texture::class)?.textures?.clear()
        else {
            println("Setting textures")
            println("Has drawer? ${entity.get(DrawerAttribute::class)}")
            entity.get(Texture::class)?.setTextures(entity.get(Position::class)!!, Random)
        }
    }

    private fun createCharacterMap(): List<MutableList<Entity?>> {
        val characterMap = List(sizeY) {
            MutableList<Entity?>(sizeX) {null}
        }

        characterArray.forEach {
            characterMap[it.get(Position::class)!!.y][it.get(Position::class)!!.x] = it
        }
        return characterMap
    }

    /** Returns topmost item on the tile or null */
    fun getTopItem(xPos: Int, yPos: Int): Entity? {
        val tile = map[yPos][xPos]
        if (tile[tile.size - 1] is Item)
            return tile[tile.size - 1]
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
}
