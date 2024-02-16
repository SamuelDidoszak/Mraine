package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.neutrino.ChunkManager
import com.neutrino.GameStage
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.ChunkCoords
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.generation.CharacterGenerator
import com.neutrino.game.map.generation.GenerateLevel
import com.neutrino.game.map.generation.MapTagInterpretation
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.level.Chunk
import com.neutrino.game.utility.serialization.KryoObj
import squidpony.squidmath.Coord
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.measureNanoTime

class LevelInitialization (private val gameStage: GameStage) {

    fun initializeLevel(chunkCoords: ChunkCoords, playerCoords: Coord?) {
        val previousChunk = gameStage.chunk
        if (previousChunk != null) {
            saveLevel(previousChunk)
            Turn.unsetLevel()
        }

        val chunk = loadLevel(chunkCoords) ?: GenerateLevel().generate(chunkCoords)

        val sameCoords = previousChunk?.chunkCoords?.x == chunkCoords.x && previousChunk.chunkCoords.y == chunkCoords.y
        val levelDrawer: LevelDrawer
        if (sameCoords)
            levelDrawer = ChunkManager.getDrawer(previousChunk!!)
        else {
            levelDrawer = LevelDrawer(chunk)
            ChunkManager.addChunk(chunk, levelDrawer)
            gameStage.addActor(levelDrawer)
        }

        if (sameCoords)
            levelDrawer.chunk = chunk
        else {
            gameStage.addActor(levelDrawer)
            levelDrawer.fogOfWar.initializeFogOfWar()
        }

        levelDrawer.initializeTextures(chunk.randomGenerator)
        levelDrawer.initializeCharacterTextures(chunk.characterMap)

        Turn.setLevel(chunk)
        gameStage.chunk = chunk

        gameStage.gameCamera.setCameraToEntity(Player)

//        if (previousLevel != null)
//            Player.move(Player.getPosition())
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

    /** TODO Temporary **/
    private fun addPlayer(chunk: Chunk) {
        val characterGenerator = CharacterGenerator(GenerationParams(
            MapTagInterpretation(listOf()), chunk.randomGenerator, chunk, chunk.map))
        characterGenerator.addPlayerAtStairs()
        chunk.characterArray.addAll(characterGenerator.characterArray)
    }

    private fun printData(chunk: Chunk) {
        println("Level: $chunk")
        println(chunk.movementMap)

        println("characters")
        println("Is null ${chunk.characterArray == null}")
        println(chunk.characterArray)

        println("Adding characters")
        println("Level: ${chunk.chunkCoords}")

        // add player

        println("Characters:")
        chunk.characterArray.forEach { println(it.name) }
    }
}