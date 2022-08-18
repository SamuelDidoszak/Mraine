package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.Player.xPos
import com.neutrino.game.domain.model.map.Level
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

class Initialize {

    val level: Level = Level(
        "test level",
        0,
        0,
        0,
        "A level for testing map generation",
        LevelChunkSize,
        LevelChunkSize,
        0f,
        0f
    )

    fun initialize() {
        level.provideTextures()
        // for whatever reason this is needed for level to access correct LevelChunkSize values
        println(level.sizeX.toString() + ", " + level.sizeY.toString())
    }

    fun setRandomPlayerPosition() {
        var xPos: Int
        var yPos: Int
        do {
            xPos = RandomGenerator.nextInt(0, level.sizeX)
            yPos = RandomGenerator.nextInt(0, level.sizeY)
        } while (!level.doesAllowCharacter(xPos, yPos))

        Player.xPos = xPos
        Player.yPos = yPos
    }
}