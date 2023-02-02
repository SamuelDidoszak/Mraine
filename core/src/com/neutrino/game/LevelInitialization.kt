package com.neutrino.game

import com.neutrino.GameStage
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.GetLevel
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import squidpony.squidmath.Coord

class LevelInitialization (
    private val gameStage: GameStage
) {

    private val startXPosition = 0f
    private val startYPosition = Constants.LevelChunkSize * 64f

    fun initializeLevel(levelChunkCoords: LevelChunkCoords, playerCoords: Coord?) {
        val previousLevel = gameStage.level
        if (previousLevel != null) {
            gameStage.actors.removeAll { true }
            Turn.unsetLevel()
            previousLevel.dispose()
            gameStage.animatedArray.clear()
        }

        val level: Level = GetLevel()(levelChunkCoords)
        level.characterArray.forEach {level.addActor(it)}
        Turn.setLevel(level)
        gameStage.addActor(level)
        level.initialize()
        gameStage.level = level
        gameStage.startXPosition = startXPosition
        gameStage.startYPosition = startYPosition

        gameStage.animatedArray.addAll(level.characterArray)
        gameStage.camera.position.set(Player.x, Player.y, gameStage.camera.position.z)
        level.prepareLights()

        if (previousLevel != null)
            Player.move(Player.getPosition())
    }
}