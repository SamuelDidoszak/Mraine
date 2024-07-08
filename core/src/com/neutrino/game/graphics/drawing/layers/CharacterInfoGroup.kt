package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.characters.attributes.Name
import com.neutrino.game.entities.shared.attributes.Texture

class CharacterInfoGroup: LayeredGroup(z = 1) {

    val hpBar = HpBar()
    val name = Name()
    val margin = 5f

    override fun onEntityAttached() {
//        attach()
        entity.addAttribute(hpBar)
        entity.addAttribute(name)
        add(hpBar)
        add(name, 16f)
    }

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        yOffset = entity.get(Texture::class)!!.getHeightScaled().toFloat() + margin
        super.draw(batch, x, y, parentAlpha)
    }

    override fun drawDebug(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        yOffset = entity.get(Texture::class)!!.getHeightScaled().toFloat() + margin
        super.drawDebug(batch, x, y, parentAlpha)
    }
}