package com.neutrino.game.map.generation

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.neutrino.GameStage
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.map.generation.worldgen.ChunkGenerator
import com.neutrino.game.map.generation.worldgen.WorldContext
import com.neutrino.game.map.generation.worldgen.util.ChunkCoords
import com.neutrino.game.utility.serialization.KryoObj
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.measureNanoTime

class ChunkInitialization(private val gameStage: GameStage) {

    private val chunkGenerator = ChunkGenerator(WorldContext())
    private var initializedFirstLevel = false

    fun initializeChunk(chunkCoords: ChunkCoords): Chunk {
        val previousChunk: Chunk?
        if (initializedFirstLevel) {
            previousChunk = Turn.currentChunk
//            saveLevel(previousChunk)
            Turn.unsetLevel()
        } else
            previousChunk = null
        initializedFirstLevel = true

//        val chunk = loadLevel(chunkCoords) ?: chunkGenerator.generate(chunkCoords)
        val chunk = chunkGenerator.generate(chunkCoords)

        val sameZLevel = previousChunk?.chunkCoords?.x == chunkCoords.x && previousChunk.chunkCoords.y == chunkCoords.y
        val levelDrawer: LevelDrawer
        if (sameZLevel)
            levelDrawer = ChunkManager.getDrawer(previousChunk!!)
        else {
            levelDrawer = LevelDrawer(chunk)
            ChunkManager.addChunk(chunk, levelDrawer)

            if (Player hasNot Position::class) {
                PlayerMapManager().addPlayer(chunk)
                ChunkManager.characterMethods.playerChunk = chunk.chunkCoords
                ChunkManager.characterMethods.initializeFov(chunk.chunkCoords)
                Turn.setLevel(chunk)
            }

            gameStage.addActor(levelDrawer)
            val drawerXOffset = ChunkManager.getDrawer(ChunkManager.middleChunk).x +
                    (chunk.chunkCoords.x - ChunkManager.middleChunk.chunkCoords.x) * levelDrawer.width
            val drawerYOffset = ChunkManager.getDrawer(ChunkManager.middleChunk).y -
                    (chunk.chunkCoords.y - ChunkManager.middleChunk.chunkCoords.y) * levelDrawer.height
            levelDrawer.setPosition(drawerXOffset, drawerYOffset)
        }

        if (sameZLevel)
            levelDrawer.chunk = chunk
        else {
            gameStage.addActor(levelDrawer)
            levelDrawer.fogOfWar.initializeFogOfWar()
        }

        levelDrawer.initializeTextures(chunk.randomGenerator)
        levelDrawer.initializeCharacterTextures(chunk.characterArray)

        if (sameZLevel)
            gameStage.gameCamera.setCameraToEntity(Player)

        return chunk
    }


    private fun saveLevel(chunk: Chunk) {
        val file = Gdx.files.local("saves/${chunk.id}")
        val fileOutputStream = FileOutputStream(file.file())
        val output = Output(fileOutputStream)

        Log.NONE()
        val writeNs = measureNanoTime {
            KryoObj.kryo.writeObject(output, chunk)
        }
        output.close()
        println("write:\t$writeNs")
    }

    private fun loadLevel(chunkCoords: ChunkCoords): Chunk? {
        val id: Int = chunkCoords.toHash()
        val file = Gdx.files.local("saves/$id")
        if (!file.exists())
            return null
        val fileInputStream = FileInputStream(file.file())
        val input = Input(fileInputStream)
        val chunk: Chunk
        Log.NONE()
        val readNs = measureNanoTime {
            chunk =
                KryoObj.kryo.readObject(input, Chunk::class.java)
        }
        println("read:\t$readNs")
        input.close()

        return chunk
    }
}