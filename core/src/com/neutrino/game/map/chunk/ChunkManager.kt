package com.neutrino.game.map.chunk

import com.neutrino.game.domain.model.characters.utility.Fov
import com.neutrino.game.domain.model.turn.Turn.characterArray
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.chunk.util.ChunkManagerMethods
import com.neutrino.game.util.Constants
import squidpony.squidai.DijkstraMap
import squidpony.squidgrid.Measurement
import squidpony.squidmath.Coord

object ChunkManager: ChunkManagerMethods {

    private val chunkCoordMap: MutableMap<Int, Chunk> = mutableMapOf()
    private val chunkDrawerMap: HashMap<Chunk, LevelDrawer> = HashMap()
    private var chunkMap: List<MutableList<Chunk?>> = List(3) { MutableList<Chunk?>(3) { null } }
    val middleChunk: Chunk
        get() = chunkMap[1][1]!!

    val characterMethods = CharacterMethods()

    fun addChunk(chunk: Chunk, levelDrawer: LevelDrawer) {
        chunkCoordMap[chunk.chunkCoords.toHash()] = chunk
        chunkDrawerMap[chunk] = levelDrawer
    }

    fun removeChunk(chunk: Chunk) {
        chunkCoordMap.remove(chunk.chunkCoords.toHash())
        chunkDrawerMap.remove(chunk)
    }

    fun getDrawer(chunk: Chunk): LevelDrawer {
        return chunkDrawerMap[chunk]!!
    }

    fun setMiddleChunk(chunk: Chunk) {
        val chunkCoords = chunk.chunkCoords
        for (y in -1 .. 1) {
            for (x in -1 .. 1) {
                chunkMap[y + 1][x + 1] = chunkCoordMap[
                    ChunkCoords(chunkCoords.x + x, chunkCoords.y + y, chunkCoords.z).toHash()]
            }
        }
        characterMethods.resetMap()
    }

    /**
     * @return Corrected position with correct chunk
     */
    fun getCorrectPosition(position: Position): Position {
        val xChunkDiff: Int = position.x / Constants.LevelChunkSize
        val yChunkDiff: Int = position.y / Constants.LevelChunkSize
        if (xChunkDiff == 0 && yChunkDiff == 0)
            return position
        val chunkCoords = position.chunk.chunkCoords
        return Position(
            position.x - xChunkDiff * Constants.LevelChunkSize,
            position.y - yChunkDiff * Constants.LevelChunkSize,
            chunkCoordMap[ChunkCoords(chunkCoords.x + xChunkDiff, chunkCoords.y + yChunkDiff, chunkCoords.z).toHash()]!!)
    }

    class CharacterMethods() {

        private var fullMap:  List<List<MutableList<Entity>>> = listOf(listOf(EntityList()))
        private val fov = Fov(fullMap)
        val dijkstraMap = DijkstraMap()
        private var mapImpassableList: ArrayList<Coord> = ArrayList()

        fun resetMap() {
            fullMap = generateMap()
            fov.map = middleChunk.map
//            fov.map = fullMap

            // terrain cost can be easily added by calling the initializeCost method.
            dijkstraMap.measurement = Measurement.EUCLIDEAN
            dijkstraMap.initialize(createDijkstraMap(middleChunk))
            mapImpassableList = generateImpassableList(middleChunk)
        }

        fun moveCharacter(entity: Entity, position: Position) {
            val entityPosition = entity.get(Position::class)!!
            entityPosition.chunk.characterMap[entityPosition.y][entityPosition.x] = null
            position.chunk.characterMap[position.y][position.x] = entity
            // TODO ECS Actions
//        this.addAction(Actions.moveTo(xPos * 64f, parent.height - yPos * 64f, speed))
            if (position.x != entityPosition.x)
                entity.get(Texture::class)!!.textures.mirror(position.x < entityPosition.x)
            entityPosition.x = position.x
            entityPosition.y = position.y
            entityPosition.chunk = position.chunk
        }

        // TODO Multiple Chunks
        fun updateFov(entity: Entity) {
            fov.updateFov(
                entity.get(Position::class)!!.x,
                entity.get(Position::class)!!.y,
                entity.getSuper(Ai::class)!!.fov,
                entity.getSuper(Ai::class)!!.viewDistance)
        }

        fun getPath(entity: Entity, position: Position): List<Coord> {
            val entityPosition = entity.get(Position::class)!!
            val moveList = dijkstraMap.findPath(
                30, 30,  getImpassable(), null,
                entityPosition.getPosition(), position.getPosition())
            dijkstraMap.reset()
//        entityPosition.chunk.dijkstraMap.clearGoals()
            return moveList
            // TODO MULTIPLE CHUNKS Make this method return List<Position>
//        return moveList.map { Position(it, entityPosition.chunk) }
        }

        fun addImpassable(position: Position) {
            mapImpassableList.add(Coord.get(position.x, position.y))
        }

        fun removeImpassable(position: Position) {
            mapImpassableList.remove(Coord.get(position.x, position.y))
        }

        private fun getImpassable(): List<Coord> {
            return mapImpassableList.plus(characterArray.getImpassable())
        }


        private fun generateMap(): List<List<MutableList<Entity>>> {
            val map = List(3 * Constants.LevelChunkSize) {
                List(3 * Constants.LevelChunkSize) { EntityList() } }

            for (y in 0 until 3) {
                for (x in 0 until 3) {
                    if (chunkMap[y][x] == null)
                        continue
                    val chunkMap = chunkMap[y][x]!!.map
                    for (cY in chunkMap.indices) {
                        for (cX in chunkMap[0].indices) {
                            map[y * Constants.LevelChunkSize + cY][x * Constants.LevelChunkSize + cX].addAll(chunkMap[cY][cX])
                        }
                    }
                }
            }
            return map
        }
    }

    private fun createDijkstraMap(chunk: Chunk): Array<out CharArray> {
        val movementMap: Array<out CharArray> = Array(chunk.sizeY) {CharArray(chunk.sizeX) {'.'} }
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
                    if (!entity.get(MapParams::class)!!.allowCharacterOnTop && entity hasNot ChangesImpassable::class) {
                        movementMap[x][y] = '#'
                        break
                    }
                }
            }
        }
        return movementMap
    }

    private fun generateImpassableList(chunk: Chunk): ArrayList<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
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