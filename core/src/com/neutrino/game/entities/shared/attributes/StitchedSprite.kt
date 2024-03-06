package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.Cloneable

class StitchedSprite: Attribute(), Cloneable<StitchedSprite> {
    override fun clone(): StitchedSprite = StitchedSprite()
}