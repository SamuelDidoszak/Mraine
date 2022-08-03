package com.neutrino.game.domain.model.map

import com.badlogic.gdx.graphics.Texture
import com.neutrino.game.LevelChunkSize
import com.neutrino.game.domain.use_case.map.GetMap
import com.neutrino.game.domain.use_case.map.MapUseCases

val mapUsecases = MapUseCases(GetMap())

class Level(
    val name: String,
    val xIndex: Int,
    val yIndex: Int,
    val zIndex: Int,
    val description: String?,
    val sizeX: Int = LevelChunkSize,
    val sizeY: Int = LevelChunkSize,
) {
    val id: Int = "$xIndex-$yIndex-$zIndex".hashCode()
    val textureList: ArrayList<Texture>
        get() {
            TODO()
        }

    val map: Map = Map(id,
        name = "$name $zIndex",
        xMax = sizeX,
        yMax = sizeY,
        map = mapUsecases.getMap(this)
    )

    val characterMap: CharacterMap? = null

    fun initiateTextureList() {
        for (y in 0 until map.yMax) {
            for (x in 0 until map.xMax) {
                for (z in 0 until map.map[y][x].size) {
                    var exists = false
                    val textureSrc = map.map[y][x][z].textureSrc

                    for (t in 0 until textureList.size) {
                        if (textureList[t].toString() == textureSrc) {
                            exists = true
                            continue
                        }
                    }
                    if (exists)
                        textureList.add(Texture(textureSrc))
                }
            }
        }
    }

}
