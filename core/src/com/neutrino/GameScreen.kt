package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.neutrino.game.Initialize
import com.neutrino.game.LevelChunkSize
import com.neutrino.game.Render
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Action
import com.neutrino.game.domain.model.turn.Turn
import ktx.app.KtxScreen

class GameScreen: KtxScreen {
    private val initialize: Initialize = Initialize()
    val render: Render = Render(initialize.level)
    private val extendViewport: ExtendViewport = ExtendViewport(1600f, 900f);
    private val startXPosition = 0f
    private val startYPosition = LevelChunkSize * 16f
    val player: Player = Player

    private val stage = GameStage(extendViewport)
    val batch: Batch = stage.batch

    init {
        extendViewport.camera.position.set(800f, 400f, 0f)
        Gdx.input.inputProcessor = stage
        initialize.initialize()
        stage.level = initialize.level
        stage.startXPosition = startXPosition
        stage.startYPosition = startYPosition

        extendViewport.camera.position.set((startXPosition + initialize.level.sizeX) * 8,
            (startYPosition + initialize.level.sizeY.toFloat()) * 2/3f, 0f)

        Turn.setLevel(initialize.level)
        stage.addActor(initialize.level)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 0f)
        extendViewport.apply()
        batch.projectionMatrix = extendViewport.camera.combined
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // game events such as player input and ai
        gameEvents()

        render.addAnimations()
        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        /** Here, dispose of every static state and every thread, because they can survive restarting the application */
        batch.dispose()
    }

    override fun resume() {
        /** Here, Recreate every OpenGL generated texture and references to shaders  */
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        extendViewport.update(width, height)
    }

    fun gameEvents() {
        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            stage.waitForPlayerInput = true

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && stage.clickedCoordinates == null) {
                val tile = Player.ai.getMove()
                Player.ai.action = Action.MOVE(tile.x, tile.y)
            }

            if (Player.ai.action is Action.NOTHING) {
                if (stage.clickedCoordinates == null) return
                // get coordinates
                val x = stage.clickedCoordinates!!.x
                val y = stage.clickedCoordinates!!.y

                val clickedCharacter = Turn.characterArray.get(x, y)

                if(clickedCharacter == Player)
                    Player.ai.action = Action.WAIT
                // Attack the enemy
                else if (clickedCharacter != null && Player.ai.canAttack(x, y))
                    Player.ai.action = Action.ATTACK(x, y) // can pass a character
                // Calculate move list and move to the tile
                else {
                    Player.ai.setMoveList(x, y, Turn.dijkstraMap, Turn.charactersUseCases.getImpassable())
                    val coord = Player.ai.getMove()
                    Player.ai.action = Action.MOVE(coord.x, coord.y)
                }
            }

            // reset stage to wait for input
            stage.waitForPlayerInput = false
            stage.clickedCoordinates = null
        }
        Turn.makeTurn()
    }

}