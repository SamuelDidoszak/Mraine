package com.neutrino.game.map.chunk

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.ActionBlock
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.characters.attributes.PlayerAi
import com.neutrino.game.entities.characters.callables.OnMoveCallable
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.util.ChunkManagerMethods
import com.neutrino.game.map.chunk.util.Fov
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.ChunkSize
import com.neutrino.game.util.Constants.SCALE
import com.neutrino.game.util.position
import kotlin.random.Random

object ChunkManager: ChunkManagerMethods {

    private val chunkCoordMap: MutableMap<Int, Chunk> = mutableMapOf()
    private val chunkDrawerMap: HashMap<Chunk, LevelDrawer> = HashMap()

    private var middleChunkSet = false
    var middleChunk: Chunk = Chunk(ChunkCoords(0, 0, 0))
        private set(value) { if (!middleChunkSet)
            middleChunkSet = true
            field = value
        }

    fun getEntitiesAt(position: Position): EntityList {
        return getCorrectPosition(position).chunk.map[position.y][position.x]
    }

    fun addEntityAt(position: Position, entity: Entity, mapParams: MapParams = MapParams(true, true)) {
        entity.addAttribute(DrawPosition())
        entity.addAttribute(mapParams)
        entity.addAttribute(position.clone())
        if (entity.get(Texture::class)!!.textures.isEmpty())
            entity.get(Texture::class)!!.setTextures(entity.get(Position::class), Random)
        position.chunk.map[position.y][position.x].add(entity)
    }

    fun addEntityAt(position: Position, entities: List<Entity>, mapParams: MapParams = MapParams(true, true)) {
        entities.forEach {
            it.addAttribute(DrawPosition())
            it.addAttribute(mapParams.clone())
            it.addAttribute(position.clone())
            if (it.get(Texture::class)!!.textures.isEmpty())
                it.get(Texture::class)!!.setTextures(it.get(Position::class), Random)
        }
        position.chunk.map[position.y][position.x].addAll(entities)
    }

    val characterMethods = CharacterMethods()

    fun addChunk(chunk: Chunk, levelDrawer: LevelDrawer) {
        chunkCoordMap[chunk.chunkCoords.x * 100000 + chunk.chunkCoords.y] = chunk
        chunkDrawerMap[chunk] = levelDrawer
        if (!middleChunkSet)
            middleChunk = chunk
        characterMethods.dijkstra.initializeChunk(chunk)
    }

    fun removeChunk(chunk: Chunk) {
        chunkCoordMap.remove(chunk.chunkCoords.x * 100000 + chunk.chunkCoords.y)
        chunkDrawerMap.remove(chunk)
        characterMethods.dijkstra.removeChunk(chunk)
    }

    fun getChunk(chunkCoords: ChunkCoords): Chunk? {
        return chunkCoordMap[chunkCoords.x * 100000 + chunkCoords.y]
    }

    fun getChunk(xOffset: Int, yOffset: Int): Chunk? {
        val x = middleChunk.chunkCoords.x + xOffset
        val y = middleChunk.chunkCoords.y + yOffset
        return chunkCoordMap[x * 100000 + y]
    }

    fun getDrawer(chunk: Chunk): LevelDrawer {
        return chunkDrawerMap[chunk]!!
    }

    fun isChunkLoaded(chunkCoords: ChunkCoords): Boolean = getChunk(chunkCoords) != null

    /**
     * @return Corrected position with correct chunk
     */
    fun getCorrectPosition(position: Position, xDiff: Int, yDiff: Int): Position {
        return getCorrectPosition(Position(position.x + xDiff, position.y + yDiff, position.chunk.chunkCoords))
    }

    /**
     * @return Corrected position with correct chunk
     */
    fun getCorrectPosition(position: Position): Position {
        if (position.x in 0 until ChunkSize && position.y in 0 until ChunkSize)
            return position
        
        val xOffset = if (position.x >= 0) position.x / ChunkSize else position.x / ChunkSize - 1
        val yOffset = if (position.y >= 0) -1 * position.y / ChunkSize else -1 * position.y / ChunkSize + 1

        val y = if (position.y >= 0) position.y % ChunkSize else yOffset * ChunkSize + position.y

        return Position(
            position.x - xOffset * ChunkSize,
            y,
            ChunkCoords(position.chunkCoords.x + xOffset, position.chunkCoords.y + yOffset, position.chunkCoords.z)
        )
    }

    class CharacterMethods() {

        private var fullMap:  List<List<MutableList<Entity>>> = listOf(listOf(EntityList()))
        private val fov = Fov(fullMap)

        lateinit var playerChunk: ChunkCoords

        private val walkingCharacterList: ArrayList<Character> = ArrayList()
        val dijkstra = Dijkstra()

        fun initializeFov(chunkCoords: ChunkCoords) {
            fullMap = generateMap()
            fov.map = fullMap
        }

        fun moveCharacter(entity: Entity, position: Position) {
            val entityPosition = entity.get(Position::class)!!
            val position = position.getCorrectPosition()
            entityPosition.chunk.characterMap[entityPosition.y][entityPosition.x] = null
            position.chunk.characterMap[position.y][position.x] = entity
            val mirror =
                if (position.toWorldTilePos().x == entityPosition.toWorldTilePos().x)
                    entity.get(Texture::class)!!.textures.isMirrored()
                else position.toWorldTilePos().x < entityPosition.toWorldTilePos().x

            val entityDrawPosition = entity.get(DrawPosition::class)!!

            val xDiff = (position.toWorldTilePos().x - entityPosition.toWorldTilePos().x) * 16 * SCALE
            val yDiff = -1 * (position.toWorldTilePos().y - entityPosition.toWorldTilePos().y) * 16 * SCALE

            if (position.chunkCoords != entityPosition.chunkCoords)
                entity.get(Texture::class)!!.textures.changeChunk(position.chunk)

            entityPosition.x = position.x
            entityPosition.y = position.y
            entityPosition.chunkCoords = position.chunkCoords
            entity.getSuper(Ai::class)!!.updateFov()
            entity.call(VisionChangedCallable::class)

            entityDrawPosition.x -= xDiff
            entityDrawPosition.y -= yDiff

            // if there are movement bugs, it may be because there were multiple movement calls and actions stacked
            entity.addAttribute(ActionBlock())
            if (!entity.get(Texture::class)!!.textures[0].name.endsWith("Walk"))
                (entity as Character).setAnimation("walk")
            entity.get(Texture::class)!!.textures.mirror(mirror)
            entity.addAction(Action.Sequence(
                Action.MoveBy(xDiff, yDiff, Constants.MoveSpeed * entity.get(DefensiveStats::class)!!.movementSpeed.toFloat()),
                Action.Custom {
                    entity.removeAttribute(ActionBlock::class)
                    // Setting idle animations properly
                    if (!Player.get(PlayerAi::class)!!.playerMoving ||
                        (entity != Player && entity.getSuper(Ai::class)!!.moveList.isEmpty() &&
                                !(entity.getSuper(Ai::class)!!.canAttack(Player.position) &&
                                Player.get(PlayerAi::class)!!.playerMoving)))
                        (entity as Character).setAnimation("idle")
                    else
                        walkingCharacterList.add(entity as Character)
                    entity.get(Texture::class)!!.textures.mirror(mirror)
                }
            ))
            entity.call(OnMoveCallable::class)
        }

        fun stopWalkAnimations() {
            walkingCharacterList.forEach { it.setAnimation("idle") }
            walkingCharacterList.clear()
        }

        // TODO Multiple Chunks
        fun updateFov(entity: Entity) {
            fov.updateFov(
                entity.get(Position::class)!!.x,
                entity.get(Position::class)!!.y,
                entity.getSuper(Ai::class)!!.fov,
                entity.getSuper(Ai::class)!!.viewDistance)
        }

        fun addImpassable(position: Position) {
            dijkstra.impassables.update(position, true)
        }

        fun removeImpassable(position: Position) {
            dijkstra.impassables.update(position, false)
        }

        fun isImpassable(position: Position): Boolean {
            for (entity in getEntitiesAt(position.getCorrectPosition())) {
                if (entity has ChangesImpassable::class && !entity.get(MapParams::class)!!.allowCharacterOnTop) {
                    if (entity.get(Door::class)?.open == true)
                        continue

                    return true
                }
            }
            return false
        }

        private fun generateMap(): List<List<MutableList<Entity>>> {
            val map = List(3 * ChunkSize) {
                List(3 * ChunkSize) { EntityList() } }

            for (y in 0 until 3) {
                for (x in 0 until 3) {
                    val chunk = getChunk(x - 1, y - 1) ?: continue

                    val chunkMap = chunk.map
                    for (cY in chunkMap.indices) {
                        for (cX in chunkMap[0].indices) {
                            map[y * ChunkSize + cY][x * ChunkSize + cX].addAll(chunkMap[cY][cX])
                        }
                    }
                }
            }
            return map
        }
    }
}