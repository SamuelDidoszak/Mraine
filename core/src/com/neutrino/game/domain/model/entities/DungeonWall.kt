package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.system.measureNanoTime

class DungeonWall: Entity() {
    override val allowOnTop = false
    override val allowCharacterOnTop = false
    override val name = "Dungeon wall"
    override val description = "The wall of a dungeon. It wouldn't be fun if it suddenly collapsed"

    override val textureNames: List<String> = listOf("wall", "wall2", "wallVariation", "wallVariation2", "sewerHole", "sewerHole2",
        "wallPillar", "wallPillar2", "wallPillarVariation", "wallPillarVariation2", "wallInBetween",
        "wallHorizontalMiddle", "wallHorizontalMiddleLeft", "wallHorizontalMiddleRight",
        "wallVertical", "wallVerticalEndUp", "wallVerticalEndDown", "wallVerticalEdgeUpLeft", "wallVerticalEdgeUpRight", "wallTSection",
        "singleWall", "wallVerticalEmptyLeft", "wallVerticalEmptyRight", "wallVerticalEmptyLeftRight",
        "wallVerticalClosed", "wallVerticalEmptyLeftClosed", "wallVerticalEmptyRightClosed", "wallVerticalEmptyLeftRightClosed")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
//        print(
            measureNanoTime {
        val randVal = RandomGenerator.nextFloat() * 100

//        println("\n\n" + onMapPosition.xPos.toString() + ", " + onMapPosition.yPos)

        val entityChecker = EntityChecker(onMapPosition, "DungeonWall", skipList = listOf(7, 9))

        val defaultWall = getTextureFromEqualRange(randVal, until = 90f, textures = textureNames.subList(0, 4)) ?:
        getTextureFromEqualRange(randVal, 90f, until = 98f, textures = textureNames.subList(4, 6)) ?:
        getTextureFromEqualRange(randVal, 98f, until = 100f, textures = textureNames.subList(6, 10)) ?:
        textureNames[0]


        val textureName: String =
//            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4, 6, 7, 8, 9))) "wallInBetween" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 8))) "wallVerticalEmptyLeftRight" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 8))) "wallVerticalEmptyLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 8))) "wallVerticalEmptyRight" else
            if (entityChecker.checkAllTiles(listOf(2, 4, 6), tileSkipList = listOf(1, 3))) "wallTSection" else
            if (entityChecker.checkAllTiles(listOf(2 ,8, 6), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(2 ,8, 4), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(2 ,8), tileSkipList = listOf(1, 3))) "wallVertical" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 4))) "wallVerticalEmptyLeftRightClosed" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 3, 6))) "wallVerticalEmptyLeftRightClosed" else
            if (entityChecker.checkAllTiles(listOf(4, 2, -7))) "wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(2, 6, 9))) "wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(4, 2, 7, 3))) "wallVerticalEmptyRightClosed" else
            if (entityChecker.checkAllTiles(listOf(1, 2, 4))) "wallVerticalEdgeUpLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 3, 6))) "wallVerticalEdgeUpRight" else
            if (entityChecker.checkAllTiles(listOf(2, 4))) "wallVerticalEdgeUpLeft" else
            if (entityChecker.checkAllTiles(listOf(2, 6))) "wallVerticalEdgeUpRight" else
            if (entityChecker.checkAllTiles(listOf(4, 6), tileSkipList = listOf(1, 3)))"wallHorizontalMiddle" else
            if (entityChecker.checkAllTiles(listOf(6, 7), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleLeft" else
            if (entityChecker.checkAllTiles(listOf(8), tileSkipList = listOf(1, 3))) defaultWall else
            if (entityChecker.checkAllTiles(listOf(2), tileSkipList = listOf(1, 3)))"wallVerticalEndUp" else
            if (entityChecker.checkAllTiles(listOf(4), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleRight" else
            if (entityChecker.checkAllTiles(listOf(6), tileSkipList = listOf(1, 2, 3)))"wallHorizontalMiddleLeft" else
            if (entityChecker.checkAllTiles(listOf(), tileSkipList = listOf(1, 3)))"singleWall" else
                defaultWall

            texture = getTexture(textureName)
            }


//        )print(", ")



                /*

        val tileUpIsWall = checkTile(onMapPosition, listOf(8)) == "DungeonWall"
        val tileLeftIsWall = checkTile(onMapPosition, listOf(4)) == "DungeonWall"
        val tileRightIsWall = checkTile(onMapPosition, listOf(6)) == "DungeonWall"
        val tileDownLeftIsWall = checkTile(onMapPosition, listOf(1)) == "DungeonWall"
        val tileDownIsWall = checkTile(onMapPosition, listOf(2)) == "DungeonWall"
        val tileDownRightIsWall = checkTile(onMapPosition, listOf(3)) == "DungeonWall"

        // choose a wall texture by checking surroundings
        // Doing this with if elses pains me but I don't see any other way for now
        val textureName1: String =
            // lower corner tiles
            if (tileDownLeftIsWall && tileDownIsWall && tileDownRightIsWall   && !tileLeftIsWall && !tileRightIsWall   && !tileUpIsWall) "wallVerticalEmptyLeftRight" else
            if (tileDownLeftIsWall && tileDownIsWall && !tileDownRightIsWall   && !tileLeftIsWall && !tileRightIsWall   && !tileUpIsWall) "wallVerticalEmptyLeft" else
            if (!tileDownLeftIsWall && tileDownIsWall && tileDownRightIsWall   && !tileLeftIsWall && !tileRightIsWall   && !tileUpIsWall) "wallVerticalEmptyRight" else
            // mid corner tiles
            if (tileLeftIsWall && tileDownIsWall && tileRightIsWall   && !tileUpIsWall) "wallTSection" else
            if (tileLeftIsWall && tileDownIsWall && !tileRightIsWall   && !tileUpIsWall) "wallVerticalEdgeUpLeft" else
            if (!tileLeftIsWall && tileDownIsWall && tileRightIsWall   && !tileUpIsWall) "wallVerticalEdgeUpRight" else
            // vertical tiles
            if (tileUpIsWall && tileDownIsWall) "wallVertical" else
            if (tileUpIsWall && !tileDownIsWall) defaultWall else
            if (!tileUpIsWall && tileDownIsWall) "wallVerticalEndUp" else
            // horizontal tiles
            if (tileLeftIsWall && tileRightIsWall) "wallHorizontalMiddle" else
            if (tileLeftIsWall && !tileRightIsWall) "wallHorizontalMiddleRight" else
            if (!tileLeftIsWall && tileRightIsWall) "wallHorizontalMiddleLeft" else
                "singleWall"

            texture = getTexture(textureName1)
                 */


//        val textureName: String =
//            if (!tileUpIsWall) {
//                if (tileLeftIsWall && tileRightIsWall) {
//                    if (tileDownIsWall)
//                        "wallTSection"
//                    else
//                        "wallHorizontalMiddle"
//                } else
//                if (tileLeftIsWall && !tileRightIsWall) {
//                    if (tileDownIsWall)
//                        "wallVerticalEdgeUpLeft"
//                    else
//                        "wallHorizontalMiddleRight"
//                } else
//                if (!tileLeftIsWall && tileRightIsWall) {
//                    if (tileDownIsWall)
//                        "wallVerticalEdgeUpRight"
//                    else
//                        "wallHorizontalMiddleLeft"
//                } else
//                    "singleWall"
//
//            } else
//            if (tileUpIsWall) {
//                if (tileDownIsWall)
//                    if(tileDownLeftIsWall && !tileDownRightIsWall)
//                        "wallVerticalEmptyLeft"
//                    else if(!tileDownLeftIsWall && tileDownRightIsWall)
//                        "wallVerticalEmptyRight"
//                    else if(!tileDownLeftIsWall && !tileDownRightIsWall)
//                        "wallVerticalEmptyLeftRight"
//                    else
//                        "wallVertical"
//                else
//                    defaultWall
//            } else
//                defaultWall
//
//        texture = getTexture(textureName1)
    }
}