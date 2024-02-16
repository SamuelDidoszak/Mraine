package com.neutrino.game.domain.use_case.level

import com.neutrino.game.map.level.Chunk
import com.neutrino.game.entities.map.attributes.ChangesImpassable
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.entities.shared.util.InteractionType
import squidpony.squidmath.Coord

class GetImpassable(
    val chunk: Chunk
) {
    operator fun invoke(): List<Coord> {
        val coordList: ArrayList<Coord> = ArrayList()
        for (y in 0 until chunk.sizeY) {
            for (x in 0 until chunk.sizeX) {
                for (entity in chunk.map[y][x]) {
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