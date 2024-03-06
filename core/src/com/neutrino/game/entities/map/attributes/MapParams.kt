package com.neutrino.game.entities.map.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality

class MapParams(
    var allowOnTop: Boolean,
    var allowCharacterOnTop: Boolean
): Attribute(), Equality<MapParams>, Cloneable<MapParams> {
    override fun clone(): MapParams = MapParams(allowOnTop, allowCharacterOnTop)
    override fun isEqual(other: MapParams): Boolean = allowOnTop == other.allowOnTop && allowCharacterOnTop == other.allowCharacterOnTop
}