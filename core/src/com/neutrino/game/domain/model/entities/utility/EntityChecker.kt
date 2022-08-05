package com.neutrino.game.domain.model.entities.utility

class EntityChecker (
    private val onMapPosition: OnMapPosition,
    private val requiredEntity: String? = null,
    private val requiredEntities: List<String>? = null,
    private val forbiddenEntity: String? = null,
    private val forbiddenEntities: List<String>? = null,
    private val skipList: List<Int>? = null
) {
    private val surroundingTiles: List<MutableList<Entity>> =
        listOf(
            checkBounds(onMapPosition.yPos + 1, onMapPosition.xPos - 1), checkBounds(onMapPosition.yPos + 1, onMapPosition.xPos), checkBounds(onMapPosition.yPos + 1,onMapPosition.xPos + 1),
            checkBounds(onMapPosition.yPos, onMapPosition.xPos - 1), checkBounds(onMapPosition.yPos, onMapPosition.xPos), checkBounds(onMapPosition.yPos, onMapPosition.xPos + 1),
            checkBounds(onMapPosition.yPos - 1, onMapPosition.xPos - 1), checkBounds(onMapPosition.yPos - 1, onMapPosition.xPos), checkBounds(onMapPosition.yPos - 1, onMapPosition.xPos + 1)
        )

    private fun checkBounds(y: Int, x: Int): MutableList<Entity> {
        return if (y in onMapPosition.map.indices && x in onMapPosition.map[0].indices)
                onMapPosition.map[y][x]
            else
                mutableListOf() // can add a FalseEntity
    }

    fun checkAllTiles(directionTrueList: List<Int>,
                      tileRequiredEntity: String? = null,
                      tileRequiredEntities: List<String>? = null,
                      tileForbiddenEntity: String? = null,
                      tileForbiddenEntities: List<String>? = null,
                      tileSkipList: List<Int>? = null): Boolean {
        val finalSkipList: List<Int> = listOfNotNull(*(skipList?:listOf()).toTypedArray(), *(tileSkipList?:listOf()).toTypedArray()).filter { directionTrueList.indexOf(it * -1) == -1 }
        val finalRequiredList: List<String> = listOfNotNull(requiredEntity, tileRequiredEntity, *(requiredEntities?:listOf()).toTypedArray(), *(tileRequiredEntities?:listOf()).toTypedArray())
        val finalForbidList: List<String> = listOfNotNull(forbiddenEntity, tileForbiddenEntity, *(forbiddenEntities?:listOf()).toTypedArray(), *(tileForbiddenEntities?:listOf()).toTypedArray())
        for (i in 1 .. 9) {
            if (i == 5 || (finalSkipList.indexOf(i) != -1 && directionTrueList.indexOf(i) == -1))
                continue
            val checkValue = checkTile(i, finalRequiredList, finalForbidList)
//            println("$directionTrueList: " + (i) + ", " + (directionTrueList.indexOf(i) != -1).toString() + ", $checkValue")
            if ((directionTrueList.indexOf(i) != -1 ) != checkValue) {
//                println("returned false")
                return false
            }
        }
        return true

    }

    // possible bugs with forbidden list
    private fun checkTile(direction: Int,
        requirementList: List<String>,
        forbidList: List<String>): Boolean {
        for (requirement in requirementList) {
            var requirementsMet = false
            for (entity in surroundingTiles[direction - 1]) {
//                println(entity.javaClass.name.substring(entity.javaClass.name.lastIndexOf(".") + 1) + ", " + requirement)
                if (entity.javaClass.name.substring(entity.javaClass.name.lastIndexOf(".") + 1) == requirement) {
                    requirementsMet = true
                }
            }
            if (!requirementsMet)
                return false
        }
        for (forbid in forbidList) {
            for (entity in surroundingTiles[direction - 1])
                if (entity.javaClass.name == forbid)
                    return false
        }
        return true
    }

    fun checkDistantTile(directionList: List<Int>): String? {
        var x = onMapPosition.xPos
        var y = onMapPosition.yPos

        for (i in directionList) {
            when(i) {
                1 -> {
                    x -= 1
                    y += 1
                } 2 -> {
                y += 1
            } 3 -> {
                x += 1
                y += 1
            } 4 -> {
                x -= 1
            } 5 -> {
                // the same tile
            } 6 -> {
                x += 1
            } 7 -> {
                x -= 1
                y -= 1
            } 8 -> {
                y -= 1
            } 9 -> {
                x += 1
                y -= 1
            }
            }
        }
        if(y >= 0 && y < onMapPosition.map.size) {
            if(x >= 0 && x < onMapPosition.map[y].size) {
                val name = onMapPosition.map[y][x][0].javaClass.name
                return name.substring(name.lastIndexOf(".") + 1)
            }
        }
        return null
    }

}