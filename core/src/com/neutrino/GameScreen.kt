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
import com.neutrino.game.LevelInitialization
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.DamageNumber
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.gameplay.main.Gameplay
import com.neutrino.game.util.Constants
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import kotlin.math.absoluteValue

class GameScreen: KtxScreen {

    private val levelDrawer: LevelDrawer = LevelDrawer()
    /** Viewport for the game */
    private val extendViewport: ExtendViewport = ExtendViewport(1280f, 768f)
    private val gameStage = GameStage(extendViewport, levelDrawer)

    /** Viewport for the HUD */
    private val hudViewport = ScreenViewport()
    private val hudStage: HudStage = HudStage(hudViewport)

    /** Viewport for UI and equipment */
    private val uiViewport: ScreenViewport = ScreenViewport()
    private val uiStage: UiStage = UiStage(uiViewport, hudStage)
    private var isEqVisible: Boolean = false

    private val gameplay: Gameplay = Gameplay(gameStage, hudStage, uiStage)

    // Input multiplexers
    private val gameInputMultiplexer: InputMultiplexer = InputMultiplexer()
    private val uiInputMultiplexer: InputMultiplexer = InputMultiplexer()

    private val levelInitialization: LevelInitialization = LevelInitialization(gameStage, levelDrawer)

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

        gameStage.addActor(levelDrawer)
        levelInitialization.initializeLevel(LevelChunkCoords(0, 0, 0), null)

        gameStage.cancelSkill = gameplay::cancelUsage

        // Initiate pools
        val damagePool: Pool<DamageNumber> = Pools.get(DamageNumber::class.java)
        damagePool.fill(50)

        // Player related methods
        registerPlayerObservers()
        // General observers
        registerObservers()

//        val textureSizeLocation = shaderProgram.getUniformLocation("u_textureSize")
//        val outlineColorLocation = shaderProgram.getUniformLocation("u_outlineColor")
    }

    private fun selectInput(showEq: Boolean) {
        if (showEq == isEqVisible)
            return
        if (!showEq) {
            Gdx.input.inputProcessor = gameInputMultiplexer
            hudStage.darkenScreen(false)
            isEqVisible = false
            // drops items
            // TODO ECS ITEMS
//            while (uiStage.itemDropList.isNotEmpty())
//                gameStage.level!!.map[Player.yPos][Player.xPos].add(ItemEntity(uiStage.itemDropList.removeFirst()))
        } else {
            Gdx.input.inputProcessor = uiInputMultiplexer
            hudStage.darkenScreen(true)
            hudStage.actors.removeValue(hudStage.clickedItem, true)
            hudStage.nullifyAllValues()
            isEqVisible = true
            uiStage.tabs.showInventory()
            // refresh inventory
            if (uiStage.inventory.forceRefreshInventory) {
                uiStage.inventory.refreshInventory()
                hudStage.refreshHotBar()
                uiStage.inventory.forceRefreshInventory = false
            }
        }
    }

    override fun render(delta: Float) {
        val startNano = System.nanoTime()
        ScreenUtils.clear(0f, 0f, 0f, 0f)
        extendViewport.apply()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // game events such as player input and ai
        gameplay.gameLoop()

        AnimatedActors.animateAll()
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
            hudStage.uiMode = true
            if (!uiStage.showInventory) {
                // show normal stuff
                selectInput(showEq = false)
                // cleanup
                gameStage.showEq = false
                uiStage.showInventory = true
                hudStage.uiMode = false
            }
        }

        // Diagnostics and debug information
        hudStage.diagnostics.updateValues(startNano)
//        val cameraPosition = gameStage.getCameraPosition()
//        hudStage.diagnostics.updatePosition(cameraPosition.first, cameraPosition.second)
    }

    override fun dispose() {
        /** Here, dispose of every static state and every thread, because they can survive restarting the application */
        gameStage.batch.dispose()

        Gdx.files.local("saves/").emptyDirectory()
    }

    override fun resume() {
        /** Here, Recreate every OpenGL generated texture and references to shaders  */
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        Constants.fonts.get("equipment").setCrispness(30f).resizeDistanceField(width, height)
        Constants.fonts.get("matchup").resizeDistanceField(width, height)
        Constants.fonts.get("munro").setCrispness(30f).resizeDistanceField(width, height)
        Constants.fonts.get("schmal").setCrispness(30f).resizeDistanceField(width, height)
        Constants.fonts.get("outline").resizeDistanceField(width, height)
        Constants.fonts.get("gothic").resizeDistanceField(width, height)

        extendViewport.update(width, height)
        uiViewport.update(width, height, true)
        hudViewport.update(width, height, true)
        uiStage.updateSize(width, height)
        hudStage.updateSize(width, height)
    }

    private fun registerPlayerObservers() {
        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERHP
            // stops the player movement and focuses him on the screen
            override fun update(data: Any?): Boolean {
                if (data == -1) {
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
                if (gameStage.showEq && uiStage.inventoryManager.clickedItem == null)
                    uiStage.inventory.refreshInventory()
                else
                    uiStage.inventory.forceRefreshInventory = true

                if (data is Item) {
                    hudStage.parsePickedUpItem(data)
                }

                return true
            }
        })

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.EQUIPMENT
            override fun update(data: Any?): Boolean {
                if (data is EquipmentType || data == null) {
                    uiStage.equipment.refreshEquipment(data as EquipmentType)
                    uiStage.inventory.refreshInventory()
                    hudStage.refreshHotBar()
                }
                return true
            }
        })
    }

    private fun registerObservers() {
        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.LEVELCHANGED
            override fun update(data: Any?): Boolean {
                if (data !is LevelChunkCoords)
                    return false

                levelInitialization.initializeLevel(data, Player.getPosition())
                hudStage.diagnostics.dungeonTypeLabel.setText("Dungeon depth ${Turn.currentLevel.levelChunkCoords.z.absoluteValue}")
                return true
            }
        })
    }

}