package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pools
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants
import com.neutrino.game.LevelInitialization
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.DamageNumber
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.Destructable
import com.neutrino.game.domain.model.entities.utility.Interactable
import com.neutrino.game.domain.model.entities.utility.Interaction
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.UseOn
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.domain.use_case.level.LevelChunkCoords
import com.neutrino.game.utility.Highlighting
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import squidpony.squidmath.Coord
import kotlin.math.abs
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

        gameStage.cancelSkill = this::cancelSkill

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
            while (uiStage.itemDropList.isNotEmpty())
                gameStage.level!!.map[Player.yPos][Player.xPos].add(ItemEntity(uiStage.itemDropList.removeFirst()))
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
        gameLoop()

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

    private var waitForAdditionalClick: Boolean = false

    private fun gameLoop() {
        if ((Player.hasActions() || gameStage.focusPlayer) && !gameStage.lookingAround) {
            gameStage.setCameraToPlayer()
            gameStage.focusPlayer = !gameStage.isPlayerFocused()
        }

        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            gameStage.waitForPlayerInput = true

            useItems()

            // use skill
            val usedSkill = hudStage.usedSkill ?: uiStage.usedSkill
            if (usedSkill != null) {
                val used = useSkill(usedSkill)
                if (!used)
                    return
            }

            // interact with an entity
            if (Player.ai.action is Action.NOTHING && !Player.hasActions() && Player.ai.entityTargetCoords != null) {
                val entityCoords = Player.ai.entityTargetCoords!!
                val entity = Turn.currentLevel.getEntityWithAction(entityCoords.first, entityCoords.second) as Interactable?
                // Entity has disappeared in the meantime
                if (entity == null)
                    Player.ai.entityTargetCoords = null
                else {
                    val action = entity.getPrimaryInteraction()
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

            // WASD movement
            if (Player.ai.action is Action.NOTHING && gameStage.moveDirection != null && !Player.hasActions()) {
                val yChange = when (gameStage.moveDirection) {
                    7, 8, 9 -> -1
                    1, 2, 3 -> 1
                    else -> 0
                }
                val xChange = when (gameStage.moveDirection) {
                    1, 4, 7 -> -1
                    3, 6, 9 -> 1
                    else -> 0
                }

                val wasdCoord = Coord.get(Player.xPos + xChange, Player.yPos + yChange)
                if (!Turn.currentLevel.allowsCharacter(wasdCoord.x, wasdCoord.y) || LevelArrays.getCharacterAt(wasdCoord) != null)
                    return

                Player.ai.moveTo(wasdCoord.x, wasdCoord.y, Turn.dijkstraMap, LevelArrays.getImpassableList())
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
                    if (!Turn.currentLevel.discoveredMap[y][x] || !Turn.currentLevel.allowsCharacterChangesImpassable(x, y))
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

    private fun useItems() {
        fun addCooldownIndicator(coords: Coord) {
            val cooldownLabel = TextraLabel("[@Cozette][%600][*]Cooldown", KnownFonts.getStandardFamily())
            cooldownLabel.name = "cooldown"
            gameStage.addActor(cooldownLabel)
            cooldownLabel.setPosition(coords.x * 64f, (6400f - coords.y * 64f) + 72f)
            cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
            cooldownLabel.addAction(
                Actions.sequence(
                    Actions.delay(1.25f),
                    Actions.removeActor()))
        }

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

            uiStage.inventory.forceRefreshInventory = true
            hudStage.refreshHotBar()
        }

        // Use item on different characters
        val useItemOn = hudStage.useItemOn ?: uiStage.useItemOn
        if (useItemOn != null) {
            val range = (useItemOn as ItemType.USABLE).hasRange ?: object : HasRange {
                override var range: Int = 1
                override var rangeType: RangeType = RangeType.SQUARE
            }

            when (useItemOn.useOn) {
                UseOn.SELF_AND_OTHERS, UseOn.OTHERS_ONLY -> {
                    if (!waitForAdditionalClick) {
                        waitForAdditionalClick = true
                        gameStage.highlighting.highlightArea(range, Player.getPosition(), useItemOn.useOn == UseOn.OTHERS_ONLY, true)
                        gameStage.highlightRange = object: HasRange {
                            override var range: Int = 0
                            override var rangeType: RangeType = RangeType.SQUARE
                        }
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.ONLY_CHARACTERS
                        gameStage.skillRange = range
                    }
                    if (gameStage.clickedCoordinates == null)
                        return

                    if (!range.isInRange(Player.getPosition(), gameStage.clickedCoordinates!!)) {
                        cancelSkill()
                        return
                    }

                    val clickedCharacter: Character? = LevelArrays.getCharacterAt(gameStage.clickedCoordinates!!.x, gameStage.clickedCoordinates!!.y)
                    if (clickedCharacter == null || (useItemOn.useOn == UseOn.OTHERS_ONLY && clickedCharacter == Player)) {
                        gameStage.clickedCoordinates = null
                        return
                    }
                    if (clickedCharacter.eventArray.hasCooldown((useItemOn as? CausesCooldown)?.cooldownType)) {
                        addCooldownIndicator(gameStage.clickedCoordinates!!)
                        gameStage.clickedCoordinates = null
                        return
                    }

                    Player.ai.action = Action.ITEM(useItemOn, clickedCharacter)
                    // removing item from eq or decreasing its amount
                    val itemInEq = Player.inventory.itemList.find { it.item == useItemOn }!!
                    if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
                        itemInEq.item.amount = itemInEq.item.amount!! - 1
                    else
                        Player.inventory.itemList.remove(itemInEq)

                    uiStage.inventory.forceRefreshInventory = true
                    hudStage.refreshHotBar()
                    cancelSkill()
                }
                UseOn.TILE -> {
                    if (!waitForAdditionalClick) {
                        waitForAdditionalClick = true
                        gameStage.highlighting.highlightArea(range, Player.getPosition(), false, true)
                        gameStage.highlightRange = if (useItemOn is HasRange) useItemOn else object: HasRange {
                            override var range: Int = 0
                            override var rangeType: RangeType = RangeType.SQUARE
                        }
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                        gameStage.skillRange = range
                    }
                    if (gameStage.clickedCoordinates == null)
                        return

                    if (!range.isInRange(Player.getPosition(), gameStage.clickedCoordinates!!)) {
                        cancelSkill()
                        return
                    }

                    val character: Character? = LevelArrays.getCharacterAt(gameStage.clickedCoordinates!!)
                    if (character == null) {
                        cancelSkill()
                        return
                    }
                    if (character.eventArray.hasCooldown((useItemOn as? CausesCooldown)?.cooldownType)) {
                        addCooldownIndicator(gameStage.clickedCoordinates!!)
                        gameStage.clickedCoordinates = null
                        return
                    }

                    Player.ai.action = Action.ITEM(useItemOn, character)
                    // removing item from eq or decreasing its amount
                    val itemInEq = Player.inventory.itemList.find { it.item == useItemOn }!!
                    if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
                        itemInEq.item.amount = itemInEq.item.amount!! - 1
                    else
                        Player.inventory.itemList.remove(itemInEq)

                    uiStage.inventory.forceRefreshInventory = true
                    hudStage.refreshHotBar()
                    // TODO implement using item on tile
                    //  tile = gameStage.clickedCoordinates!!
                    cancelSkill()
                }
                else -> return
            }
        }
    }

    /**
     * Tries to use provided skill
     * @return false if skill was cancelled
     */
    private fun useSkill(usedSkill: Skill): Boolean {
        when (usedSkill) {
            is Skill.ActiveSkill -> {
                Player.ai.action = Action.SKILL(usedSkill)
                uiStage.usedSkill = null
                hudStage.usedSkill = null
            }
            is Skill.ActiveSkillCharacter -> {
                if (!waitForAdditionalClick) {
                    waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.getPosition(), true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.ONLY_CHARACTERS
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    cancelSkill()
                    return false
                }

                val clickedCharacter: Character? = LevelArrays.getCharacterAt(gameStage.clickedCoordinates!!.x, gameStage.clickedCoordinates!!.y)
                if (clickedCharacter == null) {
                    gameStage.clickedCoordinates = null
                    return false
                }

                Player.ai.action = Action.SKILL(usedSkill, clickedCharacter)
                cancelSkill()
            }
            is Skill.ActiveSkillTile -> {
                if (!waitForAdditionalClick) {
                    waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.getPosition(), true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    cancelSkill()
                    return false
                }

                Player.ai.action = Action.SKILL(usedSkill, tile = gameStage.clickedCoordinates!!)
                cancelSkill()
            }
            is Skill.ActiveSkillArea -> {
                if (!waitForAdditionalClick) {
                    waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.getPosition(), false, true)
                    gameStage.highlightRange = usedSkill.area
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    cancelSkill()
                    return false
                }

                Player.ai.action = Action.SKILL(usedSkill, tile = gameStage.clickedCoordinates!!)
                cancelSkill()
            }

            is Skill.PassiveSkill -> {
                throw Exception("Cannot use passive skill!")
            }
        }
        return true
    }

    private fun cancelSkill() {
        gameStage.highlightRange = null
        gameStage.highlightMode = Highlighting.Companion.HighlightModes.NORMAL
        waitForAdditionalClick = false
        gameStage.highlighting.deHighlight()
        gameStage.clickedCoordinates = null
        uiStage.usedSkill = null
        hudStage.usedSkill = null
        uiStage.useItemOn = null
        hudStage.useItemOn = null

        gameStage.skillRange = null
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