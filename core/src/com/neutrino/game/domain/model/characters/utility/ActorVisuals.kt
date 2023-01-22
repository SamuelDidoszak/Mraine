package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Pools
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.graphics.utility.ColorUtils

/**
 * Adds various visuals to provided actor groups
 */
object ActorVisuals {
    fun showDamage(group: Group, color: Color, number: Float) {
        val damageNumber = Pools.get(DamageNumber::class.java).obtain()
        group.addActor(damageNumber)
        damageNumber.init(ColorUtils.toHexadecimal(color), number)
    }

    /**
     * @param color provided as a hexadecimal String value of a color
     */
    fun showDamage(group: Group, color: String, number: Float) {
        val damageNumber = Pools.get(DamageNumber::class.java).obtain()
        group.addActor(damageNumber)
        damageNumber.init(color, number)
    }

    fun showItemUsed(group: Group, item: Item) {
        val itemActor = Image(item.texture)
        itemActor.setSize(itemActor.width * 4, itemActor.height * 4)
        itemActor.name = "itemUsed"

        group.addActor(itemActor)
        itemActor.setPosition(0f, group.height + 32f)
        itemActor.addAction(Actions.moveBy(0f, -32f, 1f))
        itemActor.addAction(
            Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()))
    }

    fun showPickedUpItem(group: Group, item: Item) {
        val itemActor = Image(item.texture)
        itemActor.setSize(itemActor.width * 4, itemActor.height * 4)
        itemActor.name = "item"

        group.addActor(itemActor)
        itemActor.setPosition(0f, group.height)
        itemActor.addAction(Actions.moveBy(0f, 36f, 1f))
        itemActor.addAction(
            Actions.sequence(
                Actions.fadeOut(1.25f),
                Actions.removeActor()))
    }

    fun showAiIntention(group: Group, intention: IntentionIcon) {
        group.findActor<Image>("intention")?.remove()
        val intentionActor = Image(intention.statusTexture)
        intentionActor.setSize(intentionActor.width * 4, intentionActor.height * 4)
        intentionActor.name = "intention"

        group.addActor(intentionActor)
        intentionActor.setPosition(0f, group.height + 32f)
        intentionActor.addAction(
            Actions.sequence(
                Actions.delay(intention.displayTime),
                Actions.fadeOut(0.1f),
                Actions.removeActor()))
    }
}