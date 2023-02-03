package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.utility.serialization.AtlasRegion

class SkillActor(val skill: Skill): Group(), TextureHaver, PickupActor {
    // needed for resize
    override val ogWidth = 84f
    override val ogHeight = 84f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight

    override val textureNames: List<String> = listOf(skill.textureName)
    override var texture: AtlasRegion = setTexture()
    override var mirrored: Boolean = false

    val backgroundTexture = Constants.DefaultUITexture.findRegion("skillBackground")

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

}