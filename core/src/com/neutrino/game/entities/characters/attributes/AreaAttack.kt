package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType

class AreaAttack(
    override var range: Int,
    override var rangeType: RangeType): Attribute(), HasRange