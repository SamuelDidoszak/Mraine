package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Skills
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.Constants
import ktx.actors.alpha


class SkillTreeActor(val skill: Skill.PassiveSkill): Group() {
    val texture: TextureAtlas.AtlasRegion = Textures.get(skill.textureName).texture

    private val backgroundTexture = getBackgroundDrawable()
    private val highlightOverlay = Image(Constants.DefaultUITexture.findRegion("skillHighlight"))
    private val darkenOverlay = Image(Constants.DefaultUITexture.findRegion("skillDarken"))

    init {
        name = skill.name
        width = 84f
        height = 84f
        refreshOverlay()
    }

    fun refreshOverlay() {
        removeActor(darkenOverlay)
        darkenOverlay.alpha = 1f
        if (Player.get(Skills::class)!!.has(skill::class))
            return

        if (checkRequirements()) {
            darkenOverlay.alpha = 0.5f
            addActor(darkenOverlay)
        }
        else
            addActor(darkenOverlay)
    }

    private fun checkRequirements(): Boolean {
        var passed = true
        skill.requirements?.forEach { if (!it.check(Player)) passed = false }
        var treePassed = skill.skillTreeRequirements.isEmpty()
        skill.skillTreeRequirements.forEach { if (Player.get(Skills::class)!!.has(it)) treePassed = true }
        return passed && treePassed
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(backgroundTexture, this.x, this.y , width, height)
        batch?.draw(texture, this.x + 10f, this.y + 10f, 64f, 64f)
        super.draw(batch, parentAlpha)

        // required for fading
        color.a = 1f
        batch?.color = color
    }

    fun setHighlight(add: Boolean) {
        if (add)
            addActor(highlightOverlay)
        else
            removeActor(highlightOverlay)
    }

    fun activate() {
        removeActor(darkenOverlay)
    }

    fun toX(): Float {
        return x + width / 2f
    }

    fun toY(): Float {
        return y + height
    }

    fun fromX(): Float {
        return x + width / 2f
    }

    fun fromY(): Float {
        return y
    }

    private fun getBackgroundDrawable(): TextureAtlas.AtlasRegion {
        return Constants.DefaultUITexture.findRegion(when (skill.skillType) {
            SkillType.STRENGTH -> "skillStrength"
            SkillType.DEFENCE -> "skillDefence"
            SkillType.ROGUE -> "skillRogue"
            SkillType.RANGED -> "skillRanged"
            else -> "skillBackground"
        })
    }
}