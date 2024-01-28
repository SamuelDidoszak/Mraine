package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.util.InteractionType
import squidpony.squidmath.Coord

class GetImpassable(
    val level: Level
) {
    operator fun invoke(): List<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (entity in level.map[y][x]) {
                    if (entity has ChangesImpassable::class && !entity.get(MapParams::class)!!.allowCharacterOnTop) {
                        if ((entity.get(Interaction::class)?.interactionList?.find { it is InteractionType.DOOR } as InteractionType.DOOR?)?.open == true)
                            continue

                        coordList.add(Coord.get(x, y))
                        break
                    }
                }
            }
        }
        return coordList
    }
}