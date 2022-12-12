package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.neutrino.game.Constants
import com.neutrino.game.Constants.LevelChunkSize
import com.neutrino.game.Initialize
import com.neutrino.game.Render
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.DamageNumber
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import kotlin.math.abs

class GameScreen: KtxScreen {
    private val initialize: Initialize = Initialize()
    private val render: Render = Render(initialize.level)
    private val startXPosition = 0f
    private val startYPosition = LevelChunkSize * 64f

    /** Viewport for the game */
    private val extendViewport: ExtendViewport = ExtendViewport(1600f, 900f)
    private val gameStage = GameStage(extendViewport)

    /** Viewport for the HUD */
    private val hudViewport = ScreenViewport()
    private val hudStage: HudStage = HudStage(hudViewport)

    /** Viewport for UI and equipment */
    private val uiViewport: ScreenViewport = ScreenViewport()
    private val uiStage: UiStage = UiStage(uiViewport, hudStage)
    private var isEqVisible: Boolean = false

    // Input multiplexers
    private val gameInputMultiplexer: InputMultiplexer = InputMultiplexer()
    private val uiInputMultiplexer: InputMultiplexer = InputMultiplexer()

    init {
        Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("data/uiskin.json"))
        // Initialize stages
        uiStage.initialize()
        hudStage.initialize(uiStage)
        hudStage.addStatusIcon()

        // setup input multiplexers
        if (Gdx.app.type == Application.ApplicationType.Android) {
            val gestureDetector = GestureDetector(GestureHandler(extendViewport))
            gameInputMultiplexer.addProcessor(gestureDetector)
            gameInputMultiplexer.addProcessor(gameStage)
        } else {
            gameInputMultiplexer.addProcessor(hudStage)
            gameInputMultiplexer.addProcessor(gameStage)
        }
        uiInputMultiplexer.addProcessor(hudStage)
        uiInputMultiplexer.addProcessor(uiStage)
        Gdx.input.inputProcessor = gameInputMultiplexer

        // level initialization
        initialize.initialize()
        gameStage.level = initialize.level
        gameStage.startXPosition = startXPosition
        gameStage.startYPosition = startYPosition

        Turn.setLevel(initialize.level)
        gameStage.addActor(initialize.level)
        gameStage.camera.position.set(Player.x, Player.y, gameStage.camera.position.z)

        // Initiate pools
        val damagePool: Pool<DamageNumber> = Pools.get(DamageNumber::class.java)
        damagePool.fill(50)

        // Player related methods
        registerPlayerObservers()
    }

    private fun selectInput(showEq: Boolean) {
        if (showEq == isEqVisible)
            return
        if (!showEq) {
            Gdx.input.inputProcessor = gameInputMultiplexer
            hudStage.darkenScreen(false)
            isEqVisible = false
            // drops items
            while (uiStage.itemDropList.isNotEmpty())
                gameStage.level!!.map.map[Player.yPos][Player.xPos].add(ItemEntity(uiStage.itemDropList.removeFirst()))
            hudStage.refreshHotBar()
        } else {
            Gdx.input.inputProcessor = uiInputMultiplexer
            hudStage.darkenScreen(true)
            isEqVisible = true
            uiStage.showInventory()
            // refresh inventory
            if (uiStage.forceRefreshInventory) {
                uiStage.refreshInventory()
                hudStage.refreshHotBar()
                uiStage.forceRefreshInventory = false
            }
        }
    }

    override fun render(delta: Float) {
        val startNano = System.nanoTime()
        ScreenUtils.clear(0f, 0f, 0f, 0f)
        extendViewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // game events such as player input and ai
        gameLoop()

        render.addAnimations()
        gameStage.act(delta)
        gameStage.draw()

        // Draw HUD
        hudViewport.apply()
        hudStage.act(delta)
        hudStage.draw()

        if (gameStage.showEq) {
            // show eq
            selectInput(showEq = true)
            uiStage.viewport.apply()
            uiStage.act(delta)
            uiStage.draw()
            if (!uiStage.showInventory) {
                // show normal stuff
                selectInput(showEq = false)
                // cleanup
                gameStage.showEq = false
                uiStage.showInventory = true
            }
        }

        // Diagnostics and debug information
        hudStage.diagnostics.updateValues(startNano)
        val cameraPosition = gameStage.getCameraPosition()
        hudStage.diagnostics.updatePosition(cameraPosition.first, cameraPosition.second)
    }

    override fun dispose() {
        /** Here, dispose of every static state and every thread, because they can survive restarting the application */
        gameStage.batch.dispose()
    }

    override fun resume() {
        /** Here, Recreate every OpenGL generated texture and references to shaders  */
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        extendViewport.update(width, height)
        uiViewport.update(width, height, true)
        hudViewport.update(width, height, true)
        uiStage.updateSize(width, height)
        hudStage.updateSize(width, height)
    }

    private fun gameLoop() {
        if ((Player.hasActions() || gameStage.focusPlayer) && !gameStage.lookingAround) {
            gameStage.setCameraToPlayer()
            gameStage.focusPlayer = !gameStage.isPlayerFocused()
        }

        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            gameStage.waitForPlayerInput = true

            // If an item was used in eq, make an adequate use action
            val usedItemList = hudStage.usedItemList.ifEmpty { uiStage.usedItemList }
            if (usedItemList.isNotEmpty() && !Player.hasActions()) {
                // If user clicked, stop using items
                if (gameStage.clickedCoordinates != null) {
                    // Remove all items from the list, stopping them from being used
                    while (usedItemList.isNotEmpty()) {
                        usedItemList.removeFirst()
                    }
                }
                // Use the item
                val item = usedItemList.removeFirst()
                Player.ai.action = Action.ITEM(item, Player)
                // removing item from eq or decreasing its amount
                val itemInEq = Player.inventory.itemList.find { it.item == item }!!
                if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
                    itemInEq.item.amount = itemInEq.item.amount!! - 1
                else
                    Player.inventory.itemList.remove(itemInEq)

                uiStage.forceRefreshInventory = true
                hudStage.refreshHotBar()
            }

            // interact with an entity
            if (Player.ai.action is Action.NOTHING && !Player.hasActions() && Player.ai.entityTargetCoords != null) {
                val entityCoords = Player.ai.entityTargetCoords!!
                val entity = Turn.currentLevel.getEntityWithAction(entityCoords.first, entityCoords.second) as Interactable?
                // Entity has disappeared in the meantime
                if (entity == null)
                    Player.ai.entityTargetCoords = null
                else {
                    val action = entity.getPrimaryAction()
                    if (action != null) {
                        if (action is Interaction.DESTROY)
                            action.requiredDistance = if ((entity as Destructable).destroyed) -1 else Player.range
                        // check the distance and act if close enough
                        if ((entityCoords.first in Player.xPos - action.requiredDistance .. Player.xPos + action.requiredDistance) &&
                            (entityCoords.second in Player.yPos - action.requiredDistance .. Player.yPos + action.requiredDistance)) {
                            Player.ai.action = Action.INTERACTION(entity as Entity, action)
                            // Stop moving
                            Player.ai.moveList = ArrayDeque()
                        }
                    }
                }
            }

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && gameStage.clickedCoordinates == null && Player.ai.action is Action.NOTHING) {
                if (Turn.updateBatch.firstOrNull() is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
                    Player.ai.setMoveList(Player.ai.moveList.last().x, Player.ai.moveList.last().y, Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()), true)
                val tile = Player.ai.getMove()
                Player.ai.action = Action.MOVE(tile.x, tile.y)
                if (!gameStage.lookingAround)
                    gameStage.focusPlayer = true
            }

            // Set the player action if there was no previous one
            if (Player.ai.action is Action.NOTHING) {
                // calls this method until a tile is clicked
                if (gameStage.clickedCoordinates == null) return
                // player clicked during movement
                if (Player.ai.moveList.isNotEmpty() || Player.hasActions()) {
                    Player.ai.moveList = ArrayDeque()
                    Player.ai.entityTargetCoords = null
                    gameStage.clickedCoordinates = null
                    return
                }

                // get coordinates
                val x = gameStage.clickedCoordinates!!.x
                val y = gameStage.clickedCoordinates!!.y

                val clickedCharacter = Turn.characterArray.get(x, y)

                if(clickedCharacter == Player) {
                    gameStage.focusPlayer = true
                    gameStage.lookingAround = false
                    if (Turn.currentLevel.getTopItem(x, y) != null)
                        Player.ai.action = Action.NOTHING
                    else {
                        // TODO add defend action
                        Player.ai.action = Action.WAIT
                    }
                }
                // Attack the enemy
                else if (clickedCharacter != null && Player.ai.canAttack(x, y))
                        Player.ai.action = Action.ATTACK(x, y) // can pass a character

                // Calculate move list
                if (Player.ai.action is Action.NOTHING) {
                    // Add the interactable entity as the target
                    if (Turn.currentLevel.getEntityWithAction(x, y) != null)
                        Player.ai.entityTargetCoords = Pair(x, y)
                    else
                        Player.ai.entityTargetCoords = null

                    // Add player movement list
                    if (!Turn.currentLevel.allowsCharacterChangesImpassable(x, y))
                        Player.ai.action = Action.NOTHING
                    else
                        Player.ai.setMoveList(x, y, Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))

                    // Focus player either if he's off screen or if he clicked near his current position
                    if (!gameStage.isInCamera(Player.xPos, Player.yPos) ||
                            abs(Player.xPos - x) <= 5 &&  abs(Player.yPos - y) <= 5) {
                        gameStage.lookingAround = false
                        gameStage.focusPlayer = true
                    }
                }
            }

            // reset stage to wait for input
            gameStage.waitForPlayerInput = false
            gameStage.clickedCoordinates = null
            Turn.playerAction = false
        }
        while (!Turn.playerAction)
            Turn.makeTurn()
    }

    private fun registerPlayerObservers() {
        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERHP
            // stops the player movement and focuses him on the screen
            override fun update(data: Any?): Boolean {
                if (data == true) {
                    gameStage.focusPlayer = true
                    gameStage.lookingAround = false

                    Player.ai.moveList = ArrayDeque()
                    Player.ai.entityTargetCoords = null
                }
                return false
            }
        })

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PICKUP
            override fun update(data: Any?): Boolean {
                if (gameStage.showEq && uiStage.clickedItem == null)
                    uiStage.refreshInventory()
                else
                    uiStage.forceRefreshInventory = true

                if (uiStage.clickedItem == null)
                    hudStage.refreshHotBar()

                return true
            }
        })

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.EQUIPMENT
            override fun update(data: Any?): Boolean {
                if (data is EquipmentType || data == null)
                    uiStage.refreshEquipment(data as EquipmentType)
                return true
            }
        })
    }

}