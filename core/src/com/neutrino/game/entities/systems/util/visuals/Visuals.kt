package com.neutrino.game.entities.systems.util.visuals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools
import com.neutrino.game.domain.model.characters.utility.IntentionIcon
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Drawables
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.drawables.DrawableGroup
import com.neutrino.game.graphics.drawing.drawables.DrawableText
import com.neutrino.game.graphics.drawing.drawables.DrawableTexture
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.height

object Visuals {
    fun showDamage(entity: Entity, color: Color, number: Float) {
        showDamage(entity, ColorUtils.toHexadecimal(color), number)
    }

    /**
     * @param color provided as a hexadecimal String value of a color
     */
    fun showDamage(entity: Entity, color: String, number: Float) {
        val damageNumber = Pools.get(DamageNumber::class.java).obtain()
        damageNumber.initialize(entity)
        damageNumber.init(color, number)
    }

    fun showItemUsed(entity: Entity, item: Entity) {
        val itemDraw = DrawableTexture(entity, item.get(Texture::class)!!.textures[0].clone())
        itemDraw.z = 2
        itemDraw.initialize(entity)
        itemDraw.scale = 1.5f
        itemDraw.xOffset = (64 - itemDraw.width) / 2f
        itemDraw.yOffset = entity.get(Texture::class)!!.getHeightScaled() + 32f

        itemDraw.addAction(Action.MoveBy(0f, -32f, 1f))
        itemDraw.addAction(Action.Sequence(
            Action.FadeOut(1.25f),
            Action.Delete()
        ))
    }

    fun showPickedUpItem(entity: Entity, item: Entity) {
        val itemDraw = DrawableTexture(entity, item.get(Texture::class)!!.textures[0].clone())
        itemDraw.z = 2
        itemDraw.scale = 1.5f
        itemDraw.xOffset = (64 - itemDraw.width) / 2f
        itemDraw.yOffset = entity.get(Texture::class)!!.getHeightScaled().toFloat()
        itemDraw.initialize(entity)

        itemDraw.addAction(Action.MoveBy(0f, 36f, 1f))
        itemDraw.addAction(Action.Sequence(
            Action.FadeOut(1.25f),
            Action.Delete()
        ))
    }

    fun showAiIntention(entity: Entity, intention: IntentionIcon) {
        val intentionTexture = DrawableTexture(entity, intention.statusTexture)
        intentionTexture.centerOnEntity = true
        intentionTexture.z = 2
        intentionTexture.scale = 1.5f
        intentionTexture.yOffset = entity.get(Texture::class)!!.getHeightScaled() + 32f
        intentionTexture.initialize(entity)

        intentionTexture.addAction(
            Action.Sequence(
                Action.Delay(intention.displayTime),
                Action.FadeOut(0.1f),
                Action.Delete())
        )
    }

    fun showText(entity: Entity, text: String, typingLabel: Boolean = true) {
        val textDraw = DrawableText(text, true, typingLabel,(entity.get(Texture::class)?.getWidthScaled() ?: 64) * 6)
        textDraw.centerOnEntity = true

        var textGroup = entity.get(Drawables::class)?.getDrawable { it is DrawableGroup && it.groupName == "textGroup" } as? DrawableGroup?
        if (textGroup == null) {
            textGroup = DrawableGroup()
            textGroup.centerOnEntity = true
            textGroup.groupName = "textGroup"
            textGroup.z = 3
            textGroup.yOffset = entity.height
            textGroup.initialize(entity)
        }
        textGroup.add(textDraw, 32f)

        textDraw.addAction(Action.Sequence(
            Action.Delay(1.5f + text.length / 20f),
            Action.FadeOut(1.25f),
            Action.Delete(),
            Action.Custom { if (!textGroup.hasChildren()) textGroup.detach() }
        ))
    }
}