package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.Initialize
import com.neutrino.game.Render
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.DamageNumber
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table
import kotlin.math.abs

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

    // debug stuff
    private val renderLabel = TextraLabel("[%300]RENDER", KnownFonts.getStandardFamily())
    private val fpsLabel = TextraLabel("[%300]FPS", KnownFonts.getStandardFamily())
    private val memoryLabel = TextraLabel("[%300]MEMORY", KnownFonts.getStandardFamily())
    private var totalRenderTime: Long = 0

    init {
        extendViewport.camera.position.set(800f, 400f, 0.5f)

        println("screen dimensions:")
        println(Gdx.app.graphics.width)
        println(Gdx.app.graphics.height)

        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("data/uiskin.json"))
        uiStage.initialize()
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

        val damagePool: Pool<DamageNumber> = Pools.get(DamageNumber::class.java)
        damagePool.fill(50)

        // debug
        renderLabel.name = "renderLabel"
        fpsLabel.name = "fpsLabel"
        renderLabel.alignment = Align.left
        fpsLabel.alignment = Align.left
        val debugInfo: Table = scene2d.table {
            align(Align.left)
            pad(16f)
            add(renderLabel).left()
            row().pad(4f)
            add(fpsLabel).left()
            row().pad(4f)
            add(memoryLabel).left()
        }
        debugInfo.name = "debugInfo"
        debugInfo.pack()
        stage.addActor(debugInfo)
        debugInfo.setPosition(0f, stage.startYPosition)
        debugInfo.isVisible = Gdx.app.type != Application.ApplicationType.Desktop
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
            // add dropped items here
            while (uiStage.itemDropList.isNotEmpty())
                stage.level!!.map.map[Player.yPos][Player.xPos].add(ItemEntity(uiStage.itemDropList.removeFirst()))
        } else {
            Gdx.input.inputProcessor = uiStage
            isEqVisible = true
            // refresh inventory
            uiStage.refreshInventory = true
        }
    }

    override fun render(delta: Float) {
        val startNano = System.nanoTime()
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
            //TODO force the refresh to execute only once, currently, the "uiStage.clickedItem == null" check resets the item positions of those changed while pickup icon is still visible
            if (Player.findActor<Image>("item") != null && uiStage.clickedItem == null)
                uiStage.refreshInventory = true
            selectInput(showEq = true)
            uiStage.viewport.apply()
            uiStage.act(delta)
            uiStage.draw()
            if (!uiStage.showInventory) {
                // show normal stuff
                selectInput(showEq = false)
                // cleanup
                stage.showEq = false
                uiStage.showInventory = true
            }
        }

        // showing fps
        val timeDiff = System.nanoTime() - startNano
        totalRenderTime += timeDiff
        if (totalRenderTime >= 1000000000) {
            renderLabel.setText("[%300]$timeDiff")
            fpsLabel.setText("[%300]${1000000000 / timeDiff}fps  |  ${Gdx.graphics.framesPerSecond}fps")
            memoryLabel.setText("[%300]${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576}MB")
            totalRenderTime = 0
        }
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

    /** Indicates, if there is an item to be picked up at the end of movelist */
    var pickupItem: Boolean = false

    fun gameEvents() {
        if ((Player.hasActions() || stage.focusPlayer) && !stage.lookingAround) {
            stage.setCameraToPlayer()
            stage.focusPlayer = !stage.isPlayerFocused()
        }

        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            stage.waitForPlayerInput = true

            // If an item was used in eq, make an adequate use action
            if (uiStage.usedItemList.isNotEmpty() && !Player.hasActions()) {
                // If user clicked, stop using items
                if (stage.clickedCoordinates != null) {
                    // Remove all items from the list
                    while (uiStage.usedItemList.isNotEmpty()) {
                        uiStage.usedItemList.removeFirst()
                    }
                }
                // Use the item
                val item = uiStage.usedItemList.removeFirst()
                Player.ai.action = Action.ITEM(item, Player)
                // removing item from eq or decreasing its amount
                val itemInEq = Player.inventory.itemList.find { it.item == item }!!
                if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
                    itemInEq.item.amount = itemInEq.item.amount!! - 1
                else
                    Player.inventory.itemList.remove(itemInEq)
            }

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && stage.clickedCoordinates == null && Player.ai.action is Action.NOTHING) {
                // check if the player wasn't attacked
                if (Player.findActor<TextraLabel>("damage") != null) {
                    stage.focusPlayer = true
                    stage.lookingAround = false
                    // TODO instead of removing player's planned moves, add a GUI button that resumes movement

                    // TODO check if setting targets is necessary, it causes PICKUP bug
                    Player.ai.moveList = ArrayDeque()
                    Player.ai.xTarget = Player.xPos
                    Player.ai.yTarget = Player.yPos
                    return
                }

                if (Turn.updateBatch.firstOrNull() is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
                    Player.ai.setMoveList(Player.ai.xTarget, Player.ai.yTarget, Turn.dijkstraMap, Turn.charactersUseCases.getImpassable(), true)
                val tile = Player.ai.getMove()
                Player.ai.action = Action.MOVE(tile.x, tile.y)
                if (!stage.lookingAround)
                    stage.focusPlayer = true
            }

            // If an item was at the end of movelist, set the action to PICKUP
            if (pickupItem && !Player.hasActions() && stage.clickedCoordinates == null && Player.xPos == Player.ai.xTarget && Player.yPos == Player.ai.yTarget) {
                Player.ai.action = Action.PICKUP(Player.xPos, Player.yPos)
                pickupItem = false
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

                if(clickedCharacter == Player) {
                    stage.focusPlayer = true
                    stage.lookingAround = false
                    if (Turn.currentLevel.getTopItem(x, y) != null)
                        Player.ai.action = Action.PICKUP(x, y)
                    else {
                        // TODO add defend action
                        Player.ai.action = Action.WAIT
                    }
                }
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
                    // Focus player either if he's off screen or if he clicked near his current position
                    if (!stage.isInCamera(Player.xPos, Player.yPos) ||
                            abs(Player.xPos - x) <= 5 &&  abs(Player.yPos - y) <= 5) {
                        stage.lookingAround = false
                        stage.focusPlayer = true
                    }

                    // If there was an item at the clickedTile, pick it up on arrival
                    if (Turn.currentLevel.getTopItem(x, y) != null)
                        pickupItem = true
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