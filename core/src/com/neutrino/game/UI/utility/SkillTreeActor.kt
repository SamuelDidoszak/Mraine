package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.utility.serialization.AtlasRegion

class SkillTreeActor(val skill: Skill.PassiveSkill): Group(), TextureHaver {
    override val textureNames: List<String> = listOf(skill.textureName)
    override var texture: AtlasRegion = setTexture()
    override var mirrored: Boolean = false

    private val backgroundTexture = Constants.DefaultUITexture.findRegion("skillBackground")
    private val highlightOverlay = Image(Constants.DefaultUITexture.findRegion("skillHighlight"))
    private val darkenOverlay = Image(Constants.DefaultUITexture.findRegion("skillDarken"))

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        try {
            return Constants.DefaultIconTexture.findRegion(name)
        } catch (e: NullPointerException) {
            println("TextureName:\t$name\tdoesn't exist")
            // Default texture
            return Constants.DefaultEntityTexture.findRegion(textureNames[0])
        }
    }

    init {
        name = skill.name
        width = 84f
        height = 84f
        if (Player.getPassive(skill::class) == null)
            addActor(darkenOverlay)
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
}