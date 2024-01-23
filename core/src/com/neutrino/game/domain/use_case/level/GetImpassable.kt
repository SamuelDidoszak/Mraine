package com.neutrino.game.domain.use_case.level

import com.neutrino.game.domain.model.entities.utility.ChangesImpassable
import com.neutrino.game.domain.model.entities.utility.Interactable
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.domain.model.map.Level
import squidpony.squidmath.Coord

class GetImpassable(
    val level: Level
) {
    operator fun invoke(): List<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until level.sizeY) {
            for (x in 0 until level.sizeX) {
                for (entity in level.map[y][x]) {
                    if (entity is ChangesImpassable && !entity.allowCharacterOnTop) {
                        if (entity is Interactable)
                            if ((entity.interactionList.find { it is InteractionType.DOOR } as InteractionType.DOOR?)?.open == true)
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