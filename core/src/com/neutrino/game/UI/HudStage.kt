package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.*
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.domain.model.utility.ColorUtils
import com.neutrino.game.domain.model.utility.Diagnostics
import com.neutrino.game.UI.popups.ItemContextPopup
import ktx.actors.alpha
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.scene2d
import ktx.scene2d.verticalGroup
import space.earlygrey.shapedrawer.ShapeDrawer
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.concurrent.schedule

class HudStage(viewport: Viewport): Stage(viewport) {
    private val hudAtlas = TextureAtlas("UI/hud.atlas")
    private val hudElements: Map<String, TextureAtlas.AtlasRegion> = mapOf(
        "hotBar" to hudAtlas.findRegion("hotBar"),
        "cellHotBar" to hudAtlas.findRegion("cellHotBar")
    )

    private lateinit var hotBar: HorizontalGroup
    private val hotBarBorder = Image(hudElements["hotBar"])

    private lateinit var statusIcons: VerticalGroup
    private val hpMpBarGroup: VerticalGroup = VerticalGroup()
    private lateinit var expBar: HudExpBar

    private val darkenBackground = Image(TextureRegion(Texture("UI/blackBg.png")))
    val diagnostics = Diagnostics()

    /** Stage required for passing items into the hotBar */
    private lateinit var uiStage: UiStage

    fun initialize(uiStage: UiStage) {
        this.uiStage = uiStage
        hotBarBorder.setPosition(width / 2 - hotBarBorder.width / 2, 0f)
        hotBarBorder.name = "hotBarBorder"

        hotBar = scene2d.horizontalGroup {
            for (i in 0 until 10) {
                addActor(container {
                    name = (i).toString()
                    background = TextureRegionDrawable(hudElements["cellHotBar"])
                    align(Align.bottomLeft)
                }.size(84f, 84f))
            }
        }
        hotBar.pack()
        hotBar.name = "hotBar"
        addActor(hotBar)
        hotBar.setPosition(width / 2 - hotBarBorder.width / 2 + 12, 12f)
        addActor(hotBarBorder)

        statusIcons = scene2d.verticalGroup {
            width = 80f
        }
        statusIcons.pack()
        statusIcons.name = "statusIcons"
        addActor(statusIcons)
        statusIcons.setPosition(width - statusIcons.width - 80, height - 80)
//        statusIcons.setDebug(true, true)

        // Darken the background
        addActor(darkenBackground)
        darkenBackground.alpha = 0.75f
        darkenBackground.setSize(width, height)
        darkenBackground.setPosition(0f, 0f)
        darkenBackground.name = "darkenBackground"
        darkenBackground.zIndex = 0
        darkenScreen(false)

        // Initialize diagnostics
        addActor(diagnostics)
        diagnostics.setPosition(0f, height - diagnostics.height)
        diagnostics.isVisible = Gdx.app.type != Application.ApplicationType.Desktop

        // Initialize exp bar
        expBar = HudExpBar(hotBarBorder.width)
        expBar.height = 2f
        expBar.name = "expBar"
        addActor(expBar)

        // Initialize hp and mp bars
        val barHeight = 16f
        hpMpBarGroup.addActor(HudHpBar(hotBarBorder.width, barHeight))
        val mpGroup: Group = Group()
            mpGroup.addActor(HudMpBarBackground(hotBarBorder.width, barHeight))
            mpGroup.addActor(HudMpBar(hotBarBorder.width, barHeight))
            mpGroup.height = barHeight
        hpMpBarGroup.addActor(mpGroup)
        hpMpBarGroup.pack()
        hpMpBarGroup.height = barHeight * 2
        hpMpBarGroup.name = "hpMpBarGroup"
        addActor(hpMpBarGroup)
        hpMpBarGroup.zIndex = 1

        initializeListeners()
    }

    fun darkenScreen(visible: Boolean) {
        darkenBackground.isVisible = visible
    }

    var currentScale: Float = 1f
    fun updateSize(width: Int, height: Int) {
        currentScale = uiStage.currentScale
        actors.forEach {it.setScale(currentScale)}
        hotBarBorder.setPosition(this.width / 2 - hotBarBorder.widthScaled() / 2, 0f)
        hotBar.setPosition(this.width / 2 - hotBarBorder.widthScaled() / 2 + 12 * currentScale, 12f * currentScale)
        statusIcons.setPosition(this.width - statusIcons.widthScaled() - 80 * currentScale, this.height - 80 * currentScale)
        darkenBackground.setSize(width.toFloat(), height.toFloat())
        darkenBackground.setScale(1f)
        diagnostics.setPosition(0f, height - diagnostics.heightScaled())
        expBar.setPosition(hotBarBorder.x, hotBarBorder.y + hotBarBorder.heightScaled())
        // hpMpBar position can be offset via an action too!
        hpMpBarGroup.setPosition(hotBarBorder.x, hotBarBorder.y + hotBarBorder.heightScaled() + (hpMpBarStatus * hpMpBarGroup.heightScaled() / 2 - hpMpBarGroup.heightScaled()))

        // Rounding the position to get rid of the floating point precision error
        hotBarBorder.roundPosition()
        hotBar.roundPosition()
        expBar.roundPosition()
        hpMpBarGroup.roundPosition()
    }

    private var hpMpBarStatus: Int = 0
    /** Updates the bar position.
     * @param barStatus Value 0 - 2, where 0 is hidden, 1 shows only Hp, 2 shows Hp and Mp */
    fun updateHpMpBarPosition(barStatus: Int) {
        val difference = barStatus - hpMpBarStatus
        hpMpBarGroup.addAction(Actions.moveBy(0f, difference.toFloat() * 16f * currentScale, 0.5f))
        hpMpBarStatus = barStatus
    }

    fun hideBarsOnTimeout() {
        Timer().schedule(2000) {
            when (hpMpBarStatus) {
                0 -> {
                    return@schedule
                }
                1 -> {
                    if (Player.mp.equalsDelta(Player.mpMax) && Player.hp.equalsDelta(Player.hpMax))
                        updateHpMpBarPosition(0)
                }
                2 -> {
                    var position = 2
                    if (Player.hp.equalsDelta(Player.hpMax))
                        position -= 1
                    if (Player.mp.equalsDelta(Player.mpMax))
                        position -= 1
                    updateHpMpBarPosition(position)
                }
            }
        }
    }

    fun initializeListeners() {
        GlobalData.registerObserver(object : GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERHP
            override fun update(data: Any?): Boolean {
                if (data == 0)
                    return true

                if (hpMpBarStatus == 1 && Player.hp.equalsDelta(Player.hpMax))
                    hideBarsOnTimeout()

                if (hpMpBarGroup.hasActions() || !Player.mp.equalsDelta(Player.mpMax))
                    return true

                updateHpMpBarPosition(1)
                return true
            }
        })
        GlobalData.registerObserver(object : GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERMANA
            override fun update(data: Any?): Boolean {
                if (data == 0)
                    return true

                if (hpMpBarStatus == 2 && Player.mp.equalsDelta(Player.mpMax))
                    hideBarsOnTimeout()

                if (Player.mp.equalsDelta(Player.mpMax) || hpMpBarGroup.hasActions())
                    return true

                updateHpMpBarPosition(2)
                return true
            }
        })
    }

    /** ======================================================================================================================================================
                                                                    Status related methods
     */

    fun addStatusIcon() {
        val statusIcon = Image(Constants.DefaultItemTexture.findRegion("meat"))
        statusIcon.scaleBy(4f)
        statusIcons.addActor(statusIcon)
        statusIcon.name = "meat"
        statusIcon.pack()
        statusIcons.setDebug(true, true)
    }

    /** ======================================================================================================================================================
                                                                    HotBar and item parsing related variables
     */

    private var hotBarDesignatedItems: MutableList<Item?> = MutableList(10) {null}
    private var hotBarItemList: MutableList<Item?> = MutableList(10) {null}

    private var dragItem: Boolean? = false
    private var itemClicked: Boolean? = false
    private var timeClicked: Long = 0
    var clickedItem: Actor? = null
    private var contextPopup: Table? = null

    private var originalContainer: Container<*>? = null

    val usedItemList: ArrayDeque<Item> = ArrayDeque()
    private val itemContextPopup = ItemContextPopup(usedItemList, ::nullifyAllValues)

    /** ======================================================================================================================================================
                                                                    HotBar related method
    */

    /** Adds the picked up item into its designated position. Pass an item from player inventory */
    fun parsePickedUpItem(item: Item) {
        for (i in 0..9) {
            if (item == hotBarDesignatedItems[i]) {
                setItemToPosition(i, item)
                return
            }
        }
    }

    /** Refreshes the hotBar fetching each item from inventory */
    fun refreshHotBar() {
        for (i in 0 until hotBar.children.size) {
            hotBarItemList[i] = hotBarItemList[i]?.let { getItemFromInventory(it) }
            (hotBar.children[i] as Container<*>).actor = hotBarItemList[i]?.let { EqActor(it) }
        }
    }

    /** Finds a particular item in the inventory and returns it */
    private fun getItemFromInventory(item: Item): Item? {
        return Player.inventory.itemList.find { it.item == item }?.item
    }

    private fun setItemToPosition(position: Int, item: Item?, eqActor: EqActor? = null) {
        hotBarItemList[position] = eqActor?.item ?: item
        hotBarDesignatedItems[position] = eqActor?.item ?: item
        (hotBar.children[position] as Container<*>).actor =
            eqActor ?: if (item != null) EqActor(item)
            else null
    }

    /** ======================================================================================================================================================
                                                                    Input handling
    */

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        if (button == Input.Buttons.RIGHT)
            return super.touchDown(screenX, screenY, pointer, button)

        // Sets the click behavior for the item drop
        if (clickedItem != null) {
            timeClicked = TimeUtils.millis()
            return super.touchDown(screenX, screenY, pointer, button)
        }

        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        val clickedActor = actorAtGroup(coord.x, coord.y)
        if (clickedActor != null && clickedActor is Container<*>) {
            clickedItem = clickedActor.actor
            if (clickedItem != null)
                timeClicked = TimeUtils.millis()
            super.touchDown(screenX, screenY, pointer, button)
            return true
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (clickedItem == null)
            return false

        if (dragItem == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 650
            if (dragItem!!)
                pickUpItem()
        }

        if (dragItem!!) {
            val coord: Vector2 = screenToStageCoordinates(
                Vector2(screenX.toFloat(), screenY.toFloat())
            )
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * 2, coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * 2)
            return true
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord = this.screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        val passClickToGame = clickedItem == null
        // Stop contextMenu click from propagating
        if (contextPopup != null && button == Input.Buttons.LEFT) {
            val clickedInMenu = super.touchUp(screenX, screenY, pointer, button)
            if (!clickedInMenu) {
                this.actors.removeValue(contextPopup, true)
                contextPopup = null
                return true
            }

            return clickedInMenu
        }

        if (button == Input.Buttons.LEFT) {
            if (dragItem == true)
                parseItemDrop(coord.x, coord.y)

            // Handle the item pass from uiStage
            val itemFromUi: Actor? = uiStage.originalStackItem?:uiStage.clickedItem
            if (itemFromUi != null) {
                val clickedActor = actorAtGroup(coord.x, coord.y)
                if (clickedActor != null && clickedActor is Container<*>) {
                    (itemFromUi as EqActor).setScale(1f, 1f)
                    var previousPosition: Int? = null
                    for (i in 0 .. 9) {
                        if (hotBarItemList[i] == itemFromUi.item) {
                            setItemToPosition(i, null)
                            previousPosition = i
                        }
                    }
                    uiStage.itemPassedToHud()
                    // Swaps items
                    if (previousPosition != null && hotBarItemList[clickedActor.name.toInt()] != null)
                        setItemToPosition(previousPosition, hotBarItemList[clickedActor.name.toInt()])
                    setItemToPosition(clickedActor.name.toInt(), itemFromUi.item)

                    clickedItem = null
                    itemClicked = null
                }
            }

            // Dropping the clicked item
            // TODO possibly change to "originalContainer != null && clickedItem != null" and remove itemClicked
            if (itemClicked == true && TimeUtils.millis() - timeClicked <= 200) {
                parseItemDrop(coord.x, coord.y)
                clickedItem = null
            }

            // The item was clicked
            if (clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                pickUpItem()
                itemClicked = true
                clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * 1.25f) / 2 - 6,
                    coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * 1.25f) / 2 - 9)
                return true
            }
        }
        else if (button == Input.Buttons.RIGHT && clickedItem == null) {
            println("Right click is in HUD")
            if (contextPopup != null) {
                this.actors.removeValue(contextPopup, true)
                contextPopup = null
            }
            else {
                val clickedActor = actorAtGroup(coord.x, coord.y)
                if (clickedActor != null && clickedActor is Container<*> && clickedActor.actor != null) {
                    contextPopup = itemContextPopup.createContextMenu((clickedActor.actor as EqActor).item, coord.x, coord.y)
                    if (contextPopup != null) {
                        addActor(contextPopup)
                        contextPopup?.setPosition(coord.x, coord.y)
                    }
                    return true
                }
            }
        }

        clickedItem = null
        originalContainer = null
        itemClicked = null
        dragItem = null

        if (passClickToGame && actorAtGroup(coord.x, coord.y) == null) {
            return super.touchUp(screenX, screenY, pointer, button)
        } else {
//            println("Clicked an actor: ${clickedActor.name}")
            return true
        }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (clickedItem == null)
            return super.mouseMoved(screenX, screenY)

        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        if (clickedItem != null)
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * 1.25f) / 2 - 6,
                coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * 1.25f) / 2 - 9)
        return super.mouseMoved(screenX, screenY)
    }

    override fun keyDown(keyCode: Int): Boolean {
        when (keyCode) {
            Input.Keys.NUM_1 -> {
                diagnostics.isVisible = !diagnostics.isVisible
                diagnostics.resetMaxTimes()
            }
        }
        return super.keyDown(keyCode)
    }

    /** ======================================================================================================================================================
                                                                    Item related methods
    */

    fun passedItemToUi() {
        actors.removeValue(clickedItem, true)
        clickedItem = null
    }

    private fun pickUpItem() {
        originalContainer = clickedItem!!.parent as Container<*>
        setItemToPosition(originalContainer!!.name.toInt(), null)
        addActor(clickedItem)
        clickedItem!!.setScale(1.25f, 1.25f)
    }

    private fun parseItemDrop(x: Float, y: Float) {
        if (clickedItem == null)
            return

        val clickedActor = actorAtGroup(x, y)
        if (clickedActor == null || clickedActor !is Container<*>) {
            // Clicked on the border, return the item to its original container
            if (clickedActor == hotBarBorder) {
                clickedItem!!.setScale(1f, 1f)
                originalContainer!!.actor = clickedItem
                return
            }

            // Remove item from the hotBar
            if (clickedItem != null) {
                setItemToPosition(originalContainer!!.name.toInt(), null)
                clickedItem!!.remove()
            }
            return
        }
        clickedItem!!.setScale(1f, 1f)

        this.actors.removeValue(clickedItem, true)
        // check if the cell is ocupied and act accordingly
        if (clickedActor.hasChildren()) {
            // sum item amounts
            if ((clickedItem as EqActor).item.name == (clickedActor.actor as EqActor).item.name
                && (clickedActor.actor as EqActor).item.amount != null) {
                (clickedActor.actor as EqActor).item.amount =
                    (clickedActor.actor as EqActor).item.amount?.plus((clickedItem as EqActor).item.amount!!
                    )
                return
            } else {
                setItemToPosition(originalContainer!!.name.toInt(), null, clickedActor.actor as EqActor)
            }
        }
        setItemToPosition(clickedActor.name.toInt(), null, clickedItem as EqActor)
    }

    /** Sets all values to null */
    private fun nullifyAllValues() {
        clickedItem?.addAction(Actions.removeActor())
        clickedItem = null
        originalContainer = null
        itemClicked = false
        dragItem = null
        if (contextPopup != null) {
            this.actors.removeValue(contextPopup, true)
            contextPopup = null
        }
    }

    /** ======================================================================================================================================================
                                                                    Actor related methods
    */

    private fun Actor.isIn(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.widthScaled()) <= 0 &&
            y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.heightScaled()) <= 0)

    private fun Actor.isInUnscaled(x: Float, y: Float) = (x.compareDelta(this.x * currentScale) >= 0 && x.compareDelta(this.x * currentScale + this.width * currentScale) <= 0 &&
            y.compareDelta(this.y * currentScale) >= 0 && y.compareDelta(this.y * currentScale + this.height * currentScale) <= 0)

    override fun addActor(actor: Actor?) {
        actor?.setScale(currentScale)
        super.addActor(actor)
    }

    /** Returns tab at coord position. Supports nested groups */
    private fun actorAtGroup(x: Float, y: Float): Actor? {
        var actor: Actor
        for (stageActor in Array.ArrayIterator(actors)) {
            actor = stageActor
            if (actor == darkenBackground)
                continue
            if (actor.isIn(x, y)) {
                while (actor is Group) {
                    val groupX = x - actor.x
                    val groupY = y - actor.y
                    if (actor == statusIcons)
                        println("$groupX, $groupY")
                    var changedActor: Boolean = false
                    for (child in (actor as Group).children) {
                        if (child.isInUnscaled(groupX, groupY)) {
                            if (child is Group) {
                                actor = child
                                changedActor = true
                                break
                            } else
                                return child
                        }
                    }
                    if (!changedActor)
                        break
                }
                return actor
            }
        }
        return null
    }
}

class HudHpBar(private val initialWidth: Float, private val initialHeight: Float): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    init {
        name = "hpBar"
        height = initialHeight
    }

    var previousHp = Player.hp

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(color())
        }
        if (!Player.hp.equalsDelta(previousHp)) {
            previousHp = Player.hp
            drawer?.setColor(color())
        }

        drawer!!.filledRectangle(0f, initialHeight, initialWidth * (Player.hp / Player.hpMax), initialHeight)
    }

    private fun color(): Color {
        val red = if (Player.hp / Player.hpMax >= 0.5f) 1 - (Player.hp / Player.hpMax) else 1f
        val green = if (Player.hp / Player.hpMax >= 0.5f) 1f else 2 * (Player.hp / Player.hpMax)

        return ColorUtils.applySaturation(Color(red, green, 0f, 1f), 0.8f)
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}

class HudMpBar(private val initialWidth: Float, private val initialHeight: Float): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    init {
        name = "hpBar"
        height = initialHeight
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(ColorUtils.applySaturation(Color(0f, 0f, 1f, 1f), 0.6f))
        }

        drawer!!.filledRectangle(0f, 0f, initialWidth * (Player.mp / Player.mpMax), initialHeight)
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}

class HudMpBarBackground(private val initialWidth: Float, private val initialHeight: Float): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    init {
        name = "hpBar"
        height = initialHeight
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
//            drawer!!.setColor(ColorUtils.applySaturation(Color(0f, 0f, 0.6f, 1f), 0.6f))
            drawer!!.setColor(Color(11 / 255f, 29 / 255f, 74 / 255f, 1f))
        }

        drawer!!.filledRectangle(0f, 0f, initialWidth, initialHeight)
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}

class HudExpBar(private val initialWidth: Float, private val initialHeight: Float = 4f): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    init {
        name = "hpBar"
        height = initialHeight
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(Color(80 / 255f, 208 / 255f, 121 / 255f, 1f))
        }

        drawer!!.filledRectangle(this.x, this.y, initialWidth * this.scaleX, initialHeight * this.scaleY)
//                (Player.experience / Player.mpMax), initialHeight)
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}