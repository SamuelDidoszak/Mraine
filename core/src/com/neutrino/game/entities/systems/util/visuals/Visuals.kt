package com.neutrino.game.entities.systems.util.visuals

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools
import com.neutrino.game.domain.model.characters.utility.IntentionIcon
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.layers.LayeredTexture
import com.neutrino.game.graphics.utility.ColorUtils

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
        val itemDraw = LayeredTexture(entity, item.get(Texture::class)!!.textures[0].clone())
        itemDraw.initialize(entity)
        itemDraw.width *= 2
        itemDraw.height *= 2
        itemDraw.xOffset = (entity.get(Texture::class)!!.getWidthScaled() - itemDraw.width) / 2f
        itemDraw.yOffset = entity.get(Texture::class)!!.getHeightScaled() + 32f

        itemDraw.addAction(Action.MoveBy(0f, -32f, 1f))
        itemDraw.addAction(Action.Sequence(
            Action.FadeOut(1.25f),
            Action.Delete()
        ))
    }

    fun showPickedUpItem(entity: Entity, item: Entity) {
        val itemDraw = LayeredTexture(entity, item.get(Texture::class)!!.textures[0].clone())
        itemDraw.z = 2
        itemDraw.width *= 2
        itemDraw.height *= 2
        itemDraw.xOffset = (entity.get(Texture::class)!!.getWidthScaled() - itemDraw.width) / 2f
        itemDraw.yOffset = entity.get(Texture::class)!!.getHeightScaled().toFloat()
        itemDraw.initialize(entity)

        itemDraw.addAction(Action.MoveBy(0f, 36f, 1f))
        itemDraw.addAction(Action.Sequence(
            Action.FadeOut(1.25f),
            Action.Delete()
        ))
    }

    fun showAiIntention(entity: Entity, intention: IntentionIcon) {
//        group.findActor<Image>("intention")?.remove()
//        val intentionActor = Image(intention.statusTexture)
//        intentionActor.setSize(intentionActor.width * 4, intentionActor.height * 4)
//        intentionActor.name = "intention"
//
//        group.addActor(intentionActor)
//        intentionActor.setPosition(0f, group.height + 32f)
//        intentionActor.addAction(
//            Actions.sequence(
//                Actions.delay(intention.displayTime),
//                Actions.fadeOut(0.1f),
//                Actions.removeActor()))
    }
}