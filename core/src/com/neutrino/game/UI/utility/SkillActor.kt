package com.neutrino.game.UI.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.TimedEvent
import com.neutrino.game.entities.systems.events.attributes.EventList
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.Constants
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.PI
import kotlin.math.sqrt

class SkillActor(val skill: Skill): Group(), PickupActor {
    // needed for resize
    override val ogWidth = 84f
    override val ogHeight = 84f
    private var actorWidth: Float = ogWidth
    private var actorHeight: Float = ogHeight

    private val backgroundTexture = getBackgroundDrawable()
    private val texture = Textures.get(skill.textureName).texture

    private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null

    var pickedUp = false

    init {
        name = skill.name
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val clipping = clipBegin(x, y, actorWidth, actorHeight)
        // required for fading
        val color: Color = color
        batch?.setColor(color.r, color.g, color.b, color.a * parentAlpha)

        batch?.draw(backgroundTexture, this.x, this.y , actorWidth, actorHeight)
        batch?.draw(texture, this.x + 10f, this.y + 10f, actorWidth * 0.8f, actorHeight * 0.8f)
        super.draw(batch, parentAlpha)

        val cooldownEvent = getCooldownEvent()
        if (cooldownEvent != null)
            drawCooldown(batch, cooldownEvent)

        // required for fading
        color.a = 1f
        batch?.color = color
        if (clipping)
            clipEnd()
    }

    fun drawCooldown(batch: Batch?, event: TimedEvent) {
        if (drawer == null || drawer?.batch != batch) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(Color(0.2f, 0.2f, 0.2f, 0.6f))
        }

        val radius = if (pickedUp) actorWidth / 2 else actorWidth * sqrt(2f)
        drawer!!.sector(x + actorWidth / 2, y + actorHeight / 2, radius, (PI / 2).toFloat(), getCooldownPercent(event))
    }

    private fun getCooldownPercent(cooldownEvent: TimedEvent): Float {
        val cooldownPercent = cooldownEvent.turnsRemaining() / cooldownEvent.refreshTime
        return (2 * PI * cooldownPercent).toFloat()
    }

    private fun getCooldownEvent(): TimedEvent? {
        val playerEvents = Player.get(EventList::class) ?: return null
        return playerEvents.events.firstOrNull { it.event is Cooldown &&
                (it.event.type as? Cooldown.Type.SKILL)?.skill?.name == skill.name }
    }

    override fun setScale(scaleX: Float, scaleY: Float) {
        super.setScale(scaleX, scaleY)
        actorWidth = ogWidth * scaleX
        actorHeight = ogHeight * scaleY
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