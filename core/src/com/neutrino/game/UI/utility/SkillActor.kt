package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.Constants

class SkillActor(val skill: Skill): Group(), PickupActor {
    // needed for resize
    override val ogWidth = 84f
    override val ogHeight = 84f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight

    private val backgroundTexture = getBackgroundDrawable()
    private val texture = Textures.get(skill.textureName).texture

    init {
        name = skill.name
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color: com.badlogic.gdx.graphics.Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(backgroundTexture, this.x, this.y , actorWidth, actorHeight)
        batch?.draw(texture, this.x + 10f, this.y + 10f, actorWidth * 0.8f, actorHeight * 0.8f)
        super.draw(batch, parentAlpha)

        // required for fading
        color.a = 1f
        batch?.color = color
    }

    override fun setScale(scaleX: Float, scaleY: Float) {
        super.setScale(scaleX, scaleY)
        actorWidth = ogWidth * scaleX
        actorHeight = ogHeight * scaleY
    }

    fun setActive() {
        TODO()
    }

    private fun getBackgroundDrawable(): AtlasRegion {
        return Constants.DefaultUITexture.findRegion(when (skill.skillType) {
            SkillType.STRENGTH -> "skillStrength"
            SkillType.DEFENCE -> "skillDefence"
            SkillType.ROGUE -> "skillRogue"
            SkillType.RANGED -> "skillRanged"
            else -> "skillBackground"
        })
    }
}