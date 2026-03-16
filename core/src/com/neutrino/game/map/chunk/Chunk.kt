package com.neutrino.game.map.chunk

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.attributes.Chest
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.entities.map_entities.attributes.PickUp
import com.neutrino.game.entities.map_entities.util.Interactable
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.events.EventArray
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.generation.MapTag
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.ChunkSize
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import com.neutrino.game.utility.serialization.HeaderSerializable
import kotlin.random.Random

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
        get() = ChunkSize
    val sizeY: Int
        get() = ChunkSize

    @Transient
    val id: Int = chunkCoords.toHash()
    @Transient
    val randomGenerator = Random(SeedUtil.branch(Constants.Seed, chunkCoords.toString()))
    var tagList: List<MapTag> = listOf()

    private var isMapSet = false
    val map: List<List<EntityList>> = List(sizeY) { List(sizeX) { EntityList(::onEntityChanged) } }

    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    val characterArray: CharacterArray = CharacterArray()
    // Map of character locations
    @Transient
    val characterMap: List<MutableList<Entity?>> = createCharacterMap()

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    val events = EventArray()
    val globalEvents = EventArray()

    fun afterMapGeneration() {
        isMapSet = true
    }

    private fun onEntityChanged(entity: Entity, added: Boolean) {
        if (!isMapSet)
            return
        if (!added) {
            entity.get(Texture::class)?.textures?.clear()
            if (entity has ChangesImpassable::class)
                ChunkManager.characterMethods.removeImpassable(entity.get(Position::class)!!)
        } else {
            entity.addAttribute(DrawPosition())
            entity.get(Position::class)!!.setPosition(entity.x, entity.y)
            entity.get(Texture::class)?.setTextures(entity.get(Position::class)!!, Random)
            if (entity has ChangesImpassable::class)
                ChunkManager.characterMethods.addImpassable(entity.get(Position::class)!!)
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
            if (entity has PickUp::class || entity has Door::class || entity has Chest::class)
                return entity
        }
        return null
    }

    /** Returns topmost entity with provided interaction type */
    fun getEntityWithAction(xPos: Int, yPos: Int, interaction: Interactable): Entity? {
        val interactionClass = when (interaction) {
            is PickUp -> PickUp::class
            is Door -> Door::class
            is Chest -> Chest::class
            else -> throw Exception("Interaction not supported")
        }
        for (entity in map[yPos][xPos].reversed()) {
            if (entity has interactionClass)
                return entity
        }
        return null
    }
}
