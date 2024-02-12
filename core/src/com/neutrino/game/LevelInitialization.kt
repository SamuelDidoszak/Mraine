package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.neutrino.AnimatedActors
import com.neutrino.GameStage
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.ChunkCoords
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.graphics.drawing.LevelDrawer
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.generation.CharacterGenerator
import com.neutrino.game.map.generation.GenerateLevel
import com.neutrino.game.map.generation.MapTagInterpretation
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.utility.serialization.KryoObj
import squidpony.squidmath.Coord
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.system.measureNanoTime

class LevelInitialization (
    private val gameStage: GameStage,
    private val levelDrawer: LevelDrawer
) {

    fun initializeLevel(chunkCoords: ChunkCoords, playerCoords: Coord?) {
        val previousLevel = gameStage.level
        if (previousLevel != null) {
            saveLevel(previousLevel)

            levelDrawer.clearChildren()
            Turn.unsetLevel()
            previousLevel.dispose()
            AnimatedActors.clear()
            levelDrawer.clearAll()
            levelDrawer.map = levelDrawer.initializeMap()
        }

        val level = loadLevel(chunkCoords) ?: GenerateLevel(levelDrawer).generate(chunkCoords)

//        level.characterArray.forEach {levelDrawer.addActor(it)}
        // TODO ECS Characters
//        level.provideCharacterTextures()
        levelDrawer.fogOfWar.initializeFogOfWar(level)
        Turn.setLevel(level)
        gameStage.level = level

//        AnimatedActors.addAll(level.characterArray)
        gameStage.gameCamera.setCameraPosition(Player.get(DrawPosition::class)!!.x, Player.get(DrawPosition::class)!!.y)

//        if (previousLevel != null)
//            Player.move(Player.getPosition())
    }

    private fun saveLevel(level: Level) {
        val file = Gdx.files.local("saves/${level.id}")
        val fileOutputStream = FileOutputStream(file.file())
        val output = Output(fileOutputStream)

        Log.NONE()
        val writeNs = measureNanoTime {
            KryoObj.kryo.writeObject(output, level)
        }
        output.close()
        println("write:\t$writeNs")
    }

    private fun loadLevel(chunkCoords: ChunkCoords): Level? {
        val id: Int = chunkCoords.toHash()
        val file = Gdx.files.local("saves/$id")
        if (!file.exists())
            return null
        val fileInputStream = FileInputStream(file.file())
        val input = Input(fileInputStream)
        val level: Level
        Log.NONE()
        val readNs = measureNanoTime {
            level =
                KryoObj.kryo.readObject(input, Level::class.java)
        }
        println("read:\t$readNs")
        input.close()

        return level
    }

    /** TODO Temporary **/
    private fun addPlayer(level: Level) {
        val characterGenerator = CharacterGenerator(GenerationParams(
            MapTagInterpretation(listOf()), level.randomGenerator, level, level.map), levelDrawer)
        characterGenerator.addPlayerAtStairs()
        level.characterArray.addAll(characterGenerator.characterArray)
    }

    private fun printData(level: Level) {
        println("Level: $level")
        println(level.movementMap)

        println("characters")
        println("Is null ${level.characterArray == null}")
        println(level.characterArray)

        println("Adding characters")
        println("Level: ${level.chunkCoords}")

        // add player

        println("Characters:")
        level.characterArray.forEach { println(it.name) }
    }
}