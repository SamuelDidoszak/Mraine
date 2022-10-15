package com.neutrino

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.Constants
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.utility.Diagnostics
import ktx.actors.alpha
import ktx.scene2d.container
import ktx.scene2d.horizontalGroup
import ktx.scene2d.scene2d
import ktx.scene2d.verticalGroup

class HudStage(viewport: Viewport): Stage(viewport) {
    private val hudAtlas = TextureAtlas("UI/hud.atlas")
    private val hudElements: Map<String, TextureAtlas.AtlasRegion> = mapOf(
        "hotBar" to hudAtlas.findRegion("hotBar"),
        "cellHotBar" to hudAtlas.findRegion("cellHotBar")
    )

    lateinit var hotBar: HorizontalGroup
    private val hotBarBorder = Image(hudElements["hotBar"])

    lateinit var statusIcons: VerticalGroup

    private val darkenBackground = Image(TextureRegion(Texture("UI/blackBg.png")))
    val diagnostics = Diagnostics()

    fun initialize() {
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
        hotBar.setDebug(true, true)
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
        statusIcons.setDebug(true, true)

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
    }

    fun darkenScreen(visible: Boolean) {
        darkenBackground.isVisible = visible
    }

    fun update(width: Int, height: Int) {
        // TODO if the width is too small for the border to draw, rescale it or something
        hotBarBorder.setPosition(this.width / 2 - hotBarBorder.width / 2, 0f)
        hotBar.setPosition(this.width / 2 - hotBarBorder.width / 2 + 12, 12f)
        statusIcons.setPosition(this.width - statusIcons.width - 80, this.height - 80)
        darkenBackground.setSize(width.toFloat(), height.toFloat())
        diagnostics.setPosition(0f, height - diagnostics.height)
    }

    fun addStatusIcon() {
        val statusIcon = Image(Constants.DefaultItemTexture.findRegion("meat"))
        statusIcon.scaleBy(4f)
        statusIcons.addActor(statusIcon)
        statusIcon.name = "meat"
        statusIcon.pack()
        statusIcons.setDebug(true, true)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord = this.screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        val clickedActor = actorAtGroup(coord.x, coord.y)

        if (clickedActor == null) {
            return super.touchUp(screenX, screenY, pointer, button)
        } else {
            println("Clicked an actor: ${clickedActor.name}")
            return true
        }
    }

    private fun Actor.isIn(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.width) <= 0 &&
            y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.height) <= 0)

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
                    println(actor.name)
                    if (actor == statusIcons)
                        println("$groupX, $groupY")
                    var changedActor: Boolean = false
                    for (child in (actor as Group).children) {
                        if (child.isIn(groupX, groupY)) {
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

    override fun keyDown(keyCode: Int): Boolean {
        when (keyCode) {
            Input.Keys.NUM_1 -> {
                diagnostics.isVisible = !diagnostics.isVisible
            }
        }
        return super.keyDown(keyCode)
    }
}