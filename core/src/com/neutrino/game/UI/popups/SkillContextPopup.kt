package com.neutrino.game.UI.popups

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraButton
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.graphics.utility.BackgroundColor
import ktx.scene2d.Scene2DSkin

class SkillContextPopup(val skill: Skill, x: Float, y: Float, val customUseMethod: () -> Unit? = {}): Table() {
    init {
        val useButton = TextraButton("[%150][@Cozette]Use", Scene2DSkin.defaultSkin)
        useButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event?.button != Input.Buttons.LEFT)
                    return
                super.clicked(event, x, y)
                if (Player.eventArray.hasCooldown(CooldownType.SKILL(skill))) {
                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Skill is on cooldown", KnownFonts.getStandardFamily())
                    cooldownLabel.name = "cooldown"
                    parent.addActor(cooldownLabel)
                    val coords = localToParentCoordinates(Vector2(x, y))
                    cooldownLabel.setPosition(coords.x, coords.y + 8f)
                    cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                    cooldownLabel.addAction(
                        Actions.sequence(
                            Actions.fadeOut(1.25f),
                            Actions.removeActor()))
                    return
                }
                if (skill.manaCost != null && skill.manaCost!! > Player.mp) {
                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Not enough mana", KnownFonts.getStandardFamily())
                    cooldownLabel.name = "noMana"
                    parent.addActor(cooldownLabel)
                    val coords = localToParentCoordinates(Vector2(x, y))
                    cooldownLabel.setPosition(coords.x, coords.y + 8f)
                    cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                    cooldownLabel.addAction(
                        Actions.sequence(
                            Actions.fadeOut(1.25f),
                            Actions.removeActor()))
                    return
                }
                if (skill.manaCost == null && Player.eventArray.skillsOnCooldown == Player.maxSkills) {
                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Used too many skills", KnownFonts.getStandardFamily())
                    cooldownLabel.name = "tooManySkills"
                    parent.addActor(cooldownLabel)
                    val coords = localToParentCoordinates(Vector2(x, y))
                    cooldownLabel.setPosition(coords.x, coords.y + 8f)
                    cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
                    cooldownLabel.addAction(
                        Actions.sequence(
                            Actions.fadeOut(1.25f),
                            Actions.removeActor()))
                    return
                }

                customUseMethod.invoke()
            }
        })
        add(useButton).prefWidth(90f).prefHeight(40f)

        pack()

        val bgColor: BackgroundColor = BackgroundColor("UI/whiteColorTexture.png", x, y, width, height)
        bgColor.setColor(0, 0, 0, 160)
        background = bgColor
        name = "skillContextPopup"
    }
}