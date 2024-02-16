package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.graphics.drawing.EntityDrawer

/**
 * Used when entity is not a part of the map and doesn't use LevelDrawer
 * @param drawer SingleEntityDrawer or StructureDrawer
 */
class DrawerAttribute(val drawer: EntityDrawer): Attribute()