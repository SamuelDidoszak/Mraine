package com.neutrino.game.UI.popups

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.UI.UiStage
import com.neutrino.game.UI.utility.FrameButton
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Skills
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.attributes.EventList
import com.neutrino.game.entities.systems.skills.Skill

class SkillContextPopup(val skill: Skill, x: Float, y: Float, val customUseMethod: () -> Unit? = {}): Table() {
    init {
        val useButton = FrameButton("[@Cozette]Use") {}
        useButton.addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event?.button != Input.Buttons.LEFT)
                    return
                super.clicked(event, x, y)
                if (Player.get(EventList::class)?.hasCooldown(Cooldown.Type.SKILL(skill)) == true) {
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
                if (skill.manaCost != null && skill.manaCost!! > Player.get(DefensiveStats::class)!!.mp) {
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
                if (skill.manaCost == null && Player.get(EventList::class)?.skillsOnCooldown == Player.get(Skills::class)!!.maxConsecutiveSkills) {
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

                if (skill.requirements?.map { it.check(Player) }?.any { it == false } == true) {
                    val cooldownLabel = TextraLabel("[@Cozette][%600][*]Requirements are not met", KnownFonts.getStandardFamily())
                    cooldownLabel.name = "tooManySkills"
                    if (parent.stage is UiStage)
                        cooldownLabel.setScale((parent.stage as UiStage).currentScale)
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
        add(useButton).fillX()

        pack()

        name = "skillContextPopup"
    }
}