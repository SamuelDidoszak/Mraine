package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.esotericsoftware.kryo.kryo5.minlog.Log
import com.neutrino.AnimatedActors
import com.neutrino.GameStage
import com.neutrino.LevelDrawer
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.entities.CrateDoor
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.StonePillar
import com.neutrino.game.domain.model.entities.containers.Barrel
import com.neutrino.game.domain.model.entities.utility.Destructable
import com.neutrino.game.domain.model.entities.utility.Interactable
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.GetLevel
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.utility.serialization.KryoObj
import squidpony.squidmath.Coord
import java.io.FileOutputStream
import kotlin.system.measureNanoTime

class LevelInitialization (
    private val gameStage: GameStage,
    private val levelDrawer: LevelDrawer
) {

    private val startXPosition = 0f
    private val startYPosition = Constants.LevelChunkSize * 64f

    fun initializeLevel(levelChunkCoords: LevelChunkCoords, playerCoords: Coord?) {
        val previousLevel = gameStage.level
        if (previousLevel != null) {
            val file = Gdx.files.local("saves/${previousLevel.id}")
            file.writeString(Serializers.format.encodeToString(previousLevel), false)

//            file.writeString(Serializers.format.encodeToString(previousLevel), false)

//            gameStage.actors.removeAll { true }
            levelDrawer.clearChildren()
            Turn.unsetLevel()
            previousLevel.dispose()
            AnimatedActors.clear()
            levelDrawer.clearLights()
        }

        val level: Level = GetLevel()(levelChunkCoords)
        level.characterArray.forEach {levelDrawer.addActor(it)}
        level.provideTextures()
        levelDrawer.initializeLevel(level)
        Turn.setLevel(level)
        gameStage.level = level
        gameStage.startXPosition = startXPosition
        gameStage.startYPosition = startYPosition

        AnimatedActors.addAll(level.characterArray)
        gameStage.camera.position.set(Player.x, Player.y, gameStage.camera.position.z)

        if (previousLevel != null)
            Player.move(Player.getPosition())
    }
}