package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.Initialize
import com.neutrino.game.Render
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.Action
import com.neutrino.game.domain.model.turn.Turn
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin

class GameScreen: KtxScreen {
    private val initialize: Initialize = Initialize()
    val render: Render = Render(initialize.level)
    private val extendViewport: ExtendViewport = ExtendViewport(1600f, 900f)
    private val startXPosition = 0f
    private val startYPosition = LevelChunkSize * 64f
    val player: Player = Player

    private val stage = GameStage(extendViewport)
    val batch: Batch = stage.batch

    /** Viewport for UI and equipment */
    private val uiViewport: ExtendViewport = ExtendViewport(1920f, 1080f)
    private val uiStage: UiStage = UiStage(uiViewport)
    private var isEqVisible: Boolean = true // force input setup

    init {
        extendViewport.camera.position.set(800f, 400f, 0.5f)

        println("screen dimensions:")
        println(Gdx.app.graphics.width)
        println(Gdx.app.graphics.height)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("data/uiskin.json"))
        uiStage.addactor()
        selectInput(false)

        initialize.initialize()
        stage.level = initialize.level
        stage.startXPosition = startXPosition
        stage.startYPosition = startYPosition

        extendViewport.camera.position.set((startXPosition + initialize.level.sizeX) * 8,
            (startYPosition + initialize.level.sizeY.toFloat()) * 2/3f, 0f)

        Turn.setLevel(initialize.level)
        stage.addActor(initialize.level)
        stage.camera.position.set(Player.x, player.y, stage.camera.position.z)
    }

    private fun selectInput(showEq: Boolean) {
        if (showEq == isEqVisible)
            return
        if (!showEq) {
            if (Gdx.app.type == Application.ApplicationType.Android) {
                val multiplexer: InputMultiplexer = InputMultiplexer()
                val gestureDetector: GestureDetector = GestureDetector(GestureHandler(extendViewport))
                multiplexer.addProcessor(gestureDetector)
                multiplexer.addProcessor(stage)
                Gdx.input.inputProcessor = multiplexer
            } else
                Gdx.input.inputProcessor = stage
            isEqVisible = false
        } else {
            Gdx.input.inputProcessor = uiStage
            isEqVisible = true
            // refresh eq
            uiStage.refreshEq = true
        }
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

        if (stage.showEq) {
            // show eq
            selectInput(showEq = true)
            uiStage.viewport.apply()
            uiStage.act(delta)
            uiStage.draw()
            if (!uiStage.showEq) {
                // show normal stuff
                selectInput(showEq = false)
                // cleanup
                stage.showEq = false
                uiStage.showEq = true
            }
        }

        // showing fps
        Gdx.graphics.setTitle("Mraine, ${Gdx.graphics.framesPerSecond}fps")
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
        uiViewport.update(width, height)
        uiStage.updateRatio()
    }

    fun gameEvents() {
        if ((Player.hasActions() || stage.focusPlayer) && !stage.lookingAround) {
            stage.setCameraToPlayer()
            stage.focusPlayer = !stage.isPlayerFocused()
        }

        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            stage.waitForPlayerInput = true

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && stage.clickedCoordinates == null) {
                // check if the player wasn't attacked
                if (Player.findActor<TextraLabel>("damage") != null) {
                    stage.focusPlayer = true
                    stage.lookingAround = false
                    // TODO instead of removing player's planned moves, add a GUI button that resumes movement
                    Player.ai.moveList = ArrayDeque()
                    Player.ai.xTarget = Player.xPos
                    Player.ai.yTarget = Player.yPos
                    return
                }

                if (Turn.updateBatch is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
                    Player.ai.setMoveList(Player.ai.xTarget, Player.ai.yTarget, Turn.dijkstraMap, Turn.charactersUseCases.getImpassable(), true)
                val tile = Player.ai.getMove()
                Player.ai.action = Action.MOVE(tile.x, tile.y)
                if (!stage.lookingAround)
                    stage.focusPlayer = true
            }

            if (Player.ai.action is Action.NOTHING) {
                // calls this method until a tile is clicked
                if (stage.clickedCoordinates == null) return
                // player clicked during movement
                if (Player.ai.moveList.isNotEmpty() || Player.hasActions()) {
                    Player.ai.moveList = ArrayDeque()
                    Player.ai.xTarget = Player.xPos
                    Player.ai.yTarget = Player.yPos
                    stage.clickedCoordinates = null
                    return
                }

                // get coordinates
                val x = stage.clickedCoordinates!!.x
                val y = stage.clickedCoordinates!!.y

                val clickedCharacter = Turn.characterArray.get(x, y)

                if(clickedCharacter == Player)
                    Player.ai.action = Action.WAIT
                // Attack the enemy
                else
                    if (clickedCharacter != null && Player.ai.canAttack(x, y))
                    Player.ai.action = Action.ATTACK(x, y) // can pass a character
                // No character is there
                // Calculate move list and move to the tile
                else {
                    Player.ai.setMoveList(x, y, Turn.dijkstraMap, Turn.charactersUseCases.getImpassable())
                    val coord = Player.ai.getMove()
                    Player.ai.action = Action.MOVE(coord.x, coord.y)
                    stage.focusPlayer = true
                    stage.lookingAround = false
                }
            }

            // reset stage to wait for input
            stage.waitForPlayerInput = false
            stage.clickedCoordinates = null
            Turn.playerAction = false
        }
        while (!Turn.playerAction)
            Turn.makeTurn()
    }

}