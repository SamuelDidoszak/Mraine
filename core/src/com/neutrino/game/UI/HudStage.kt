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
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.UI.UiStage
import com.neutrino.game.UI.popups.Diagnostics
import com.neutrino.game.UI.popups.ItemContextPopup
import com.neutrino.game.UI.popups.SkillDetailsPopup
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.UI.utility.PickupActor
import com.neutrino.game.UI.utility.SkillActor
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.UseOn
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.*
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

    var uiMode: Boolean = false

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
                    val stats = Player.get(DefensiveStats::class)!!
                    if (stats.mp.equalsDelta(stats.mpMax) && stats.hp.equalsDelta(stats.hpMax))
                        updateHpMpBarPosition(0)
                }
                2 -> {
                    val stats = Player.get(DefensiveStats::class)!!
                    var position = 2
                    if (stats.hp.equalsDelta(stats.hpMax))
                        position -= 1
                    if (stats.mp.equalsDelta(stats.mpMax))
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
                    return false

                val stats = Player.get(DefensiveStats::class)!!
                if (hpMpBarStatus == 1 && stats.hp.equalsDelta(stats.hpMax))
                    hideBarsOnTimeout()

                if (hpMpBarGroup.hasActions() || !stats.mp.equalsDelta(stats.mpMax))
                    return false

                updateHpMpBarPosition(1)
                return false
            }
        })
        GlobalData.registerObserver(object : GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERMANA
            override fun update(data: Any?): Boolean {
                if (data == 0)
                    return false

                val stats = Player.get(DefensiveStats::class)!!
                if (hpMpBarStatus == 2 && stats.mp.equalsDelta(stats.mpMax))
                    hideBarsOnTimeout()

                if (stats.mp.equalsDelta(stats.mpMax) || hpMpBarGroup.hasActions())
                    return false

                updateHpMpBarPosition(2)
                return false
            }
        })
    }

    /** ============================================================     Status related methods     =============================================================================*/

    fun addStatusIcon() {
        val statusIcon = Image(Constants.DefaultItemTexture.findRegion("meat"))
        statusIcon.scaleBy(4f)
        statusIcons.addActor(statusIcon)
        statusIcon.name = "meat"
        statusIcon.pack()
        statusIcons.setDebug(true, true)
    }

    /** ============================================================     HotBar and item parsing related variables     =============================================================================*/

    private var hotBarDesignatedItems: MutableList<Entity?> = MutableList(10) {null}
    private var hotBarItemList: MutableList<Entity?> = MutableList(10) {null}
    private var hotBarSkillList: MutableList<Skill?> = MutableList(10) {null}

    private var dragItem: Boolean? = false
    private var itemClicked: Boolean? = false
    private var timeClicked: Long = 0
    var clickedItem: Actor? = null
    private var contextPopup: Actor? = null
    private var detailsPopup: Actor? = null

    private var originalContainer: Container<*>? = null

    val usedItemList: ArrayDeque<Entity> = ArrayDeque()
    var useItemOn: Entity? = null
    var usedSkill: Skill? = null
    private val itemContextPopup = ItemContextPopup(usedItemList, { item: Entity -> useItemOn = item}, ::nullifyAllValues)

    /** ============================================================     HotBar related methods     =============================================================================*/

    /** Adds the picked up item into its designated position. Pass an item from player inventory */
    fun parsePickedUpItem(item: Entity) {
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
            if (hotBarItemList[i] == null)
                continue
            hotBarItemList[i] = hotBarItemList[i]?.let { getItemFromInventory(it) }
            (hotBar.children[i] as Container<*>).actor = hotBarItemList[i]?.let { EqActor(it) }
        }
    }

    /** Finds a particular item in the inventory and returns it */
    private fun getItemFromInventory(item: Entity): Entity? {
        return Player.get(Inventory::class)!!.getItem(item)
    }

    private fun setItemToPosition(position: Int, item: Entity?, eqActor: EqActor? = null) {
        hotBarSkillList[position] = null
        hotBarItemList[position] = eqActor?.entity ?: item
        hotBarDesignatedItems[position] = eqActor?.entity ?: item
        (hotBar.children[position] as Container<*>).actor =
            eqActor ?: if (item != null) EqActor(item)
            else null
    }

    private fun setSkillToPosition(position: Int, skillActor: SkillActor?) {
        hotBarItemList[position] = null
        hotBarDesignatedItems[position] = null
        hotBarSkillList[position] = skillActor?.skill
        (hotBar.children[position] as Container<*>).actor = if (skillActor != null) SkillActor(skillActor.skill) else null
    }

    /** ============================================================     Input handling     =============================================================================*/

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
            dragItem = TimeUtils.millis() - timeClicked >= 450
            if (dragItem!!)
                pickUpItem()
        }

        removeContextPopup()
        removeDetailsPopup()

        if (dragItem!!) {
            val coord: Vector2 = screenToStageCoordinates(
                Vector2(screenX.toFloat(), screenY.toFloat())
            )
            clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth / 2, coord.y - (clickedItem!! as PickupActor).ogWidth / 2)
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

            if (clickedItem != null) {
                clickedItem = null
                originalContainer = null
            }
            actors.removeValue(contextPopup, true)
            contextPopup = null

            return clickedInMenu
        }

        if (button == Input.Buttons.LEFT) {
            if (dragItem == true)
                parseItemDrop(coord.x, coord.y)

            if (uiMode) {
                uiModeLeftClick(coord)

                // The item was clicked
                if (clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                    pickUpItem()
                    itemClicked = true
                    clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * 1.25f / 2 - 6,
                        coord.y - (clickedItem!! as PickupActor).ogWidth * 1.25f / 2 - 9)
                    return true
                }
            }
            else if (clickedItem != null) {
                // Check if mouse is still on the item
                val clickedActor = actorAtGroup(coord.x, coord.y)
                if (clickedActor != null && clickedActor is Container<*> && clickedItem == clickedActor.actor)
                    useActorFromHudBar(coord)
            }
        }
        else if (button == Input.Buttons.RIGHT && clickedItem == null) {
            if (contextPopup != null) {
                this.actors.removeValue(contextPopup, true)
                contextPopup = null
            }
            else {
                val clickedActor = actorAtGroup(coord.x, coord.y)
                if (clickedActor != null && clickedActor is Container<*> && clickedActor.actor != null) {
                    if (clickedActor.actor is EqActor) {
                        contextPopup = itemContextPopup.createContextMenu((clickedActor.actor as EqActor).entity, coord.x, coord.y)
                        if (contextPopup != null) {
                            addActor(contextPopup)
                            contextPopup?.setPosition(coord.x, coord.y)
                        }
                    }
                    // TODO ECS SKILLS Popups
//                    else {
//                        val skill = (clickedActor.actor as SkillActor).skill
//                        contextPopup = SkillContextPopup(skill, coord.x, coord.y) {
//                            usedSkill = skill
//                            nullifyAllValues()
//                        }
//                        addActor(contextPopup)
//                        contextPopup?.setPosition(coord.x, coord.y)
//                    }
                    return true
                }
            }
        }

        clickedItem = null
        originalContainer = null
        itemClicked = null
        dragItem = null

        if (passClickToGame && actorAtGroup(coord.x, coord.y) == null)
            return super.touchUp(screenX, screenY, pointer, button)
        return true
    }

    private fun uiModeLeftClick(coord: Vector2) {
        // Handle the item pass from uiStage
        val itemFromUi: Actor? = uiStage.inventoryManager.originalStackItem?:uiStage.inventoryManager.clickedItem
        if (itemFromUi != null) {
            val clickedActor = actorAtGroup(coord.x, coord.y)
            // Handle passing items in hud
            if (originalContainer != null) {
                clickedItem = itemFromUi
                parseItemDrop(coord.x, coord.y)
            }
            else if (clickedActor != null && clickedActor is Container<*>) {
                itemFromUi.setScale(1f, 1f)
                var previousPosition: Int? = null
                if (itemFromUi is SkillActor) {
                    for (i in 0 .. 9) {
                        if (hotBarSkillList[i] == itemFromUi.skill) {
                            setItemToPosition(i, null)
                            previousPosition = i
                        }
                    }

                    // Swaps skills
                    if (previousPosition != null && hotBarItemList[clickedActor.name.toInt()] != null)
                        setSkillToPosition(previousPosition, (hotBar.children[clickedActor.name.toInt()] as Container<*>).actor as SkillActor)
                    setSkillToPosition(clickedActor.name.toInt(), itemFromUi)
                }

                if (itemFromUi is EqActor) {
                    for (i in 0 .. 9) {
                        if (hotBarItemList[i] == itemFromUi.entity) {
                            setItemToPosition(i, null)
                            previousPosition = i
                        }
                    }
                    // Swaps items
                    if (previousPosition != null && hotBarItemList[clickedActor.name.toInt()] != null)
                        setItemToPosition(previousPosition, hotBarItemList[clickedActor.name.toInt()])
                    setItemToPosition(clickedActor.name.toInt(), itemFromUi.entity)
                }

                uiStage.inventoryManager.itemPassedToHud()

                clickedItem = null
                itemClicked = null
            }
        }

        // Dropping the clicked item
        if (itemClicked == true && TimeUtils.millis() - timeClicked <= 200) {
            parseItemDrop(coord.x, coord.y)
            clickedItem = null
        }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        if (clickedItem == null && !hotBar.isInSized(coord.x, coord.y)) {
            removeDetailsPopup()
            return super.mouseMoved(screenX, screenY)
        }

        var hoveredActor = actorAtGroup(coord.x, coord.y)
        if (!uiMode && hoveredActor != null && hoveredActor is Container<*>) {
            hoveredActor = hoveredActor.actor
            if (hoveredActor != null) {
                val popupChild = (detailsPopup as Group?)?.getChild(0)

                if (hoveredActor is SkillActor && ((detailsPopup == null || popupChild !is SkillDetailsPopup) ||
                    (popupChild.skill != hoveredActor.skill))) {
                    removeDetailsPopup()
                    val group = Group()
                    val popup = SkillDetailsPopup(hoveredActor.skill)
                    group.setSize(popup.width, popup.height)
                    group.addActor(popup)
                    detailsPopup = group
                    addActor(detailsPopup)
                    val popupCoord = hoveredActor.localToStageCoordinates(Vector2(hoveredActor.x, hoveredActor.y))
                    detailsPopup!!.setPosition(
                        popupCoord.x + hoveredActor.width * currentScale / 2 - detailsPopup!!.widthScaled() / 2f,
                        hotBarBorder.heightScaled() + 16f * currentScale)
                }
                // TODO ECS ITEMS POPUPS
//                if (hoveredActor is EqActor && ((detailsPopup == null || popupChild !is EqActor) ||
//                    (popupChild.entity != hoveredActor.entity))) {
//                    removeDetailsPopup()
//                    val group = Group()
//                    val popup =
//                        if (hoveredActor.entity has EquipmentItem::class)
//                            EquipmentComparisonPopup(hoveredActor.entity as EquipmentItem)
//                        else
//                            ItemDetailsPopup(hoveredActor.entity)
//                    group.setSize(popup.width, popup.height)
//                    group.addActor(popup)
//                    detailsPopup = group
//                    addActor(detailsPopup)
//                    val popupCoord = hoveredActor.localToStageCoordinates(Vector2(hoveredActor.x, hoveredActor.y))
//                    detailsPopup!!.setPosition(
//                        popupCoord.x + hoveredActor.width * currentScale / 2 - detailsPopup!!.widthScaled() / 2f,
//                        hotBarBorder.heightScaled() + 16f * currentScale)
//                }
            } else
                removeDetailsPopup()
        }

        if (clickedItem != null)
            clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * 1.25f / 2 - 6,
                coord.y - (clickedItem!! as PickupActor).ogWidth * 1.25f / 2 - 9)
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

    /** ============================================================     Item related methods     =============================================================================*/

    private fun pickUpItem() {
        originalContainer = clickedItem!!.parent as Container<*>
        setItemToPosition(originalContainer!!.name.toInt(), null)
        if (uiMode) {
            uiStage.inventoryManager.itemFromHud(clickedItem!!)
            return
        }

        addActor(clickedItem)
        clickedItem!!.setScale(clickedItem!!.scaleX * 1.25f, clickedItem!!.scaleY * 1.25f)
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
                uiStage.inventoryManager.itemPassedToHud()
            }
            return
        }
        clickedItem!!.setScale(1f, 1f)

        this.actors.removeValue(clickedItem, true)
        // check if the cell is ocupied and act accordingly
        if (clickedActor.hasChildren()) {
            if (clickedActor.actor is SkillActor)
                setSkillToPosition(originalContainer!!.name.toInt(), clickedActor.actor as SkillActor)
            else

            // sum item amounts
            // TODO ECS ITEMS Compare
            if ((clickedItem as EqActor).entity.name == (clickedActor.actor as EqActor).entity.name
                && (clickedActor.actor as EqActor).maxStack != 1) {
                (clickedActor.actor as EqActor).amount =
                    (clickedActor.actor as EqActor).amount + (clickedItem as EqActor).amount
                uiStage.inventoryManager.itemPassedToHud()
                return
            } else {
                setItemToPosition(originalContainer!!.name.toInt(), null, clickedActor.actor as EqActor)
            }
        }

        if (clickedItem is EqActor)
            setItemToPosition(clickedActor.name.toInt(), null, clickedItem as EqActor)
        if (clickedItem is SkillActor)
            setSkillToPosition(clickedActor.name.toInt(), clickedItem as SkillActor)

        uiStage.inventoryManager.itemPassedToHud()
        nullifyAllValues()
    }

    private fun useActorFromHudBar(coord: Vector2) {
        if (clickedItem is SkillActor) {
            val skill = (clickedItem as SkillActor).skill

            // TODO ECS EVENTS
//            if (Player.eventArray.hasCooldown(CooldownType.SKILL(skill))) {
//                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Skill is on cooldown", KnownFonts.getStandardFamily())
//                addCooldownLabel(cooldownLabel, coord)
//                return
//            }
            if (skill.manaCost != null && skill.manaCost!! > Player.get(DefensiveStats::class)!!.mp) {
                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Not enough mana", KnownFonts.getStandardFamily())
                addCooldownLabel(cooldownLabel, coord)
                return
            }
            // TODO ECS EVENTS
//            if (skill.manaCost == null && Player.eventArray.skillsOnCooldown == Player.maxSkills) {
//                val cooldownLabel = TextraLabel("[@Cozette][%600][*]Used too many skills", KnownFonts.getStandardFamily())
//                addCooldownLabel(cooldownLabel, coord)
//                return
//            }
            usedSkill = skill
            nullifyAllValues()
            return
        }

        val item = (clickedItem as EqActor).entity

        // use on others as primary action
        if (item is ItemType.USABLE && (item.useOn == UseOn.TILE || item.useOn == UseOn.OTHERS_ONLY)) {
            useItemOn = item
            return
        }

        // TODO ECS EVENTS
        // TODO ECS ITEMS USABLE
        when (item) {
//            is ItemType.EDIBLE -> {
//                if (Player.eventArray.hasCooldown((item as? CausesCooldown)?.cooldownType)) {
//                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Food is on cooldown", KnownFonts.getStandardFamily())
//                    addCooldownLabel(cooldownLabel, coord)
//                    return
//                }
//                usedItemList.add(item)
//                nullifyAllValues()
//            }
//            is SkillBook -> {
//                if (item.skill.requirement.data.containsKey("character"))
//                    (item.skill.requirement.data["character"] as Data<Character>).setData(Player)
//                if (!item.skill.requirement.checkAll()) {
//                    val requirementLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met!", KnownFonts.getStandardFamily())
//                    addCooldownLabel(requirementLabel, coord)
//                    return
//                }
//                if (Player.skillList.find { it::class == item.skill::class } != null) {
//                    val skillLearntLabel = TextraLabel("[@Cozette][%600][*]Skill is already learnt", KnownFonts.getStandardFamily())
//                    addCooldownLabel(skillLearntLabel, coord)
//                    return
//                }
//                usedItemList.add(item)
//                nullifyAllValues()
//            }
            // TODO ECS ITEMS EQUIPMENT
//            is ItemType.EQUIPMENT -> {
//                if ((item as EquipmentItem).requirements.data.containsKey("character"))
//                    (item.requirements.data["character"] as Data<Character>).setData(Player)
//                if (!item.requirements.checkAll()) {
//                    val requirementsLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met!", KnownFonts.getStandardFamily())
//                    addCooldownLabel(requirementsLabel, coord)
//                    return
//                }
//
//                val itemType = Player.equipment.setItem(item as EquipmentItem)
//                GlobalData.notifyObservers(GlobalDataType.EQUIPMENT, itemType)
//                nullifyAllValues()
//            }
//            is ItemType.USABLE -> {
//                if (Player.eventArray.hasCooldown((item as? CausesCooldown)?.cooldownType)) {
//                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Item is on cooldown", KnownFonts.getStandardFamily())
//                    addCooldownLabel(cooldownLabel, coord)
//                    return
//                }
//                usedItemList.add(item)
//                nullifyAllValues()
//            }
        }
    }

    private fun addCooldownLabel(label: TextraLabel, coord: Vector2) {
        addActor(label)
        label.setPosition(coord.x, coord.y + 8f)
        label.addAction(Actions.moveBy(0f, 36f, 1f))
        label.addAction(
            Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()))
    }

    /** Sets all values to null */
    fun nullifyAllValues() {
//        clickedItem?.addAction(Actions.removeActor())
        clickedItem = null
        originalContainer = null
        itemClicked = false
        dragItem = null
        removeContextPopup()
        removeDetailsPopup()
    }

    private fun removeContextPopup() {
        if (contextPopup != null) {
            actors.removeValue(contextPopup, true)
            contextPopup = null
        }
    }

    private fun removeDetailsPopup() {
        if (detailsPopup != null) {
            actors.removeValue(detailsPopup, true)
            detailsPopup = null
        }
    }

    /** ============================================================     Actor related methods     =============================================================================*/

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
            if (actor.isInSized(x, y)) {
                while (actor is Group) {
                    val groupX = x - actor.x
                    val groupY = y - actor.y
                    if (actor == statusIcons)
                        println("$groupX, $groupY")
                    var changedActor: Boolean = false
                    for (child in (actor as Group).children) {
                        if (child.isInUnscaled(groupX, groupY, currentScale)) {
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

    var previousHp = Player.get(DefensiveStats::class)!!.hp

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val stats = Player.get(DefensiveStats::class)!!
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(color())
        }
        if (!stats.hp.equalsDelta(previousHp)) {
            previousHp = stats.hp
            drawer?.setColor(color())
        }

        drawer!!.filledRectangle(0f, initialHeight, initialWidth * (stats.hp / stats.hpMax), initialHeight)
    }

    private fun color(): Color {
        val stats = Player.get(DefensiveStats::class)!!
        val red = if (stats.hp / stats.hpMax >= 0.5f) 1 - (stats.hp / stats.hpMax) else 1f
        val green = if (stats.hp / stats.hpMax >= 0.5f) 1f else 2 * (stats.hp / stats.hpMax)

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

        drawer!!.filledRectangle(0f, 0f,
            initialWidth * (Player.get(DefensiveStats::class)!!.mp / Player.get(DefensiveStats::class)!!.mpMax), initialHeight)
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