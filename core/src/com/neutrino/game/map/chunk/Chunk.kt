package com.neutrino.game.map.chunk

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.domain.model.characters.utility.Fov
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.attributes.Item
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.map.generation.MapTag
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.LevelChunkSize
import com.neutrino.game.utility.serialization.HeaderSerializable
import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.Measurement
import squidpony.squidmath.Coord
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
    @Transient
    val dijkstraMap = DijkstraMap()
    var mapImpassableList: ArrayList<Coord> = ArrayList()

    /**
     * A list of current level characters.
     */
    // Make it a ObjectSet or OrderedSet / OrderedMap for fast read / write / delete
    @Transient
    lateinit var characterArray: CharacterArray
    // Map of character locations
    @Transient
    lateinit var characterMap: List<MutableList<Entity?>>

    /**
     * Map of discovered and undiscovered tiles
     */
    val discoveredMap: List<MutableList<Boolean>> = List(sizeY) { MutableList(sizeX) {false} }

    @Transient
    val fov: Fov = Fov(this)

    fun afterMapGeneration() {
        isMapSet = true
        mapImpassableList = generateImpassableList()
        // terrain cost can be easily added by calling the initializeCost method.
        dijkstraMap.measurement = Measurement.EUCLIDEAN
        dijkstraMap.initialize(createDijkstraMap())
    }

    private fun onEntityChanged(entity: Entity) {
        if (!isMapSet)
            return

        println("Heyo! ${entity.name} changed!")
        dijkstraMap.initialize(createDijkstraMap())
    }

    fun getImpassable(): List<Coord> {
        return mapImpassableList.plus(characterArray.getImpassable())
    }

    private fun createDijkstraMap(): Array<out CharArray> {
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

    fun createCharacterMap(): List<MutableList<Entity?>> {
        val characterMap = List(sizeY) {
            MutableList<Entity?>(sizeX) {null}
        }

        characterArray.forEach {
            characterMap[it.get(Position::class)!!.y][it.get(Position::class)!!.x] = it
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

    fun generateImpassableList(): ArrayList<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until sizeY) {
            for (x in 0 until sizeX) {
                for (entity in map[y][x]) {
                    if (entity has ChangesImpassable::class && !entity.get(MapParams::class)!!.allowCharacterOnTop) {
                        if ((entity.get(Interaction::class)?.interactionList?.find { it is InteractionType.DOOR } as InteractionType.DOOR?)?.open == true)
                            continue

                        coordList.add(Coord.get(x, y))
                        break
                    }
                }
            }
        }
        return coordList
    }
}
