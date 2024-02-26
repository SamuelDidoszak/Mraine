import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils.Array
import com.neutrino.game.graphics.textures.*

fun get(atlas: String, region: String): AtlasRegion {
    return Textures.atlases[atlas]!!.findRegion(region)
}

fun getArray(atlas: String, vararg regions: String): Array<AtlasRegion> {
    val textureArray: Array<AtlasRegion> = Array()
    for (region in regions) {
        textureArray.add(Textures.atlases[atlas]!!.findRegion(region))
    }
    return textureArray
}

for (atlas in Gdx.files.absolute("${Gdx.files.localStoragePath}/core/src/assets/textures").list(".atlas")) {
    Textures.atlases[atlas.nameWithoutExtension()] = TextureAtlas(atlas)
}

Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$1"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$2"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$3"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$4"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$5"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$6"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$7"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$8"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$9"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$10"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$11"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$12"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$13"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$14"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$15"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$16"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$17"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorBasic$18"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorCracked$1"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorCracked$2"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorCracked$3"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorCracked$4"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$1"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$2"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$3"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$4"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$5"), z = 0)
Textures add TextureSprite(get("entities", "cleanDungeonFloorSmaller$6"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$1"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$2"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$3"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$4"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$5"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$6"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$7"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$8"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$9"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$10"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$11"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$12"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$13"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$14"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$15"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$16"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$17"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorBasic$18"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorCracked$1"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorCracked$2"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorCracked$3"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorCracked$4"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorLights$1"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorLights$2"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorLights$3"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorLights$4"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorLights$5"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$1"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$2"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$3"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$4"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$5"), z = 0)
Textures add TextureSprite(get("entities", "dungeonFloorSmaller$6"), z = 0)
Textures add TextureSprite(get("entities", "dungeonStairsDown"), z = 0)
Textures add TextureSprite(get("entities", "dungeonStairsUp"), z = 0)
Textures add TextureSprite(get("entities", "stonePillar"), 2f, 10f)
Textures add TextureSprite(get("entities", "stonePillarCracked"), 2f, 10f)
Textures add TextureSprite(get("entities", "dungeonWall$1"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWall$2"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWall$3"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWall$4"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWall$5"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWallInside"), z = 0)
Textures add TextureSprite(get("entities", "dungeonWallTop$1"), 0f, 21f)
Textures add TextureSprite(get("entities", "dungeonWallTop$2"), 0f, 21f)
Textures add TextureSprite(get("entities", "dungeonWallTop$3"), 0f, 21f)
Textures add TextureSprite(get("entities", "dungeonWallTop$4"), 0f, 21f)
Textures add TextureSprite(get("entities", "dungeonWallTop$5"), 0f, 21f)
Textures add TextureSprite(get("entities", "dungeonWallTopInside"), 0f, 16f)
Textures add TextureSprite(get("entities", "dungeonWallSide$1"))
Textures add TextureSprite(get("entities", "dungeonWallSide$2"))
Textures add TextureSprite(get("entities", "dungeonWallSide$3"))
Textures add TextureSprite(get("entities", "dungeonWallSide$4"))
Textures add TextureSprite(get("entities", "dungeonWallSide$5"))
Textures add TextureSprite(get("entities", "dungeonWallSideSmall$1"), 0f, 16f)
Textures add TextureSprite(get("entities", "dungeonWallSideSmall$2"), 0f, 16f)
Textures add TextureSprite(get("entities", "dungeonWallSideSmall$3"), 0f, 16f)
Textures add TextureSprite(get("entities", "dungeonWallSideSmall$4"), 0f, 16f)
Textures add TextureSprite(get("entities", "dungeonWallSideSmall$5"), 0f, 16f)
Textures add TextureSprite(get("entities", "woodenDoor"), 0f, 2f)
Textures add TextureSprite(get("entities", "woodenDoorClosed"))
Textures add TextureSprite(get("entities", "woodenDoorVertical"), 0f, 14f)
Textures add TextureSprite(get("entities", "woodenDoorVerticalClosed"), 13f)
Textures add TextureSprite(get("entities", "woodenDoorArched"), 0f, 2f)
Textures add TextureSprite(get("entities", "woodenDoorArchedClosed"))
Textures add TextureSprite(get("entities", "woodenDoorArchedVertical"), 0f, 14f)
Textures add TextureSprite(get("entities", "woodenDoorArchedVerticalClosed"), 13f)
Textures add TextureSprite(get("entities", "stonePillarTop"), 2f, 2f)
Textures add TextureSprite(get("entities", "candleSingle$1"), LightSources(Light(1f, 8f, Color.valueOf("e5842868"), 4f, 48f)), 10f, 8f)
Textures add TextureSprite(get("entities", "candleSingle$2"), LightSources(Light(1f, 6f, Color.valueOf("e5842868"), 4f, 48f)), 8f, 4f)
Textures add TextureSprite(get("entities", "candleSingle$3"), LightSources(Light(2f, 5f, Color.valueOf("f3642268"), 4f, 48f)), 7f, 3f)
Textures add TextureSprite(get("entities", "candleSingle$4"), LightSources(Light(2f, 8f, Color.valueOf("f3642268"), 4f, 48f)), 4f, 4f)
Textures add TextureSprite(get("entities", "candleSingle$5"), LightSources(Light(0f, 5f, Color.valueOf("dc5e2668"), 4f, 48f)), 11f, 11f)
Textures add TextureSprite(get("entities", "clayPot$1"), 1f, 3f)
Textures add TextureSprite(get("entities", "clayPot$2"), 0f, 5f)
Textures add TextureSprite(get("entities", "clayPot$3"), 4f, 4f)
Textures add TextureSprite(get("entities", "clayPot$4"), 2f, 8f)
Textures add TextureSprite(get("entities", "clayPot$1Destroyed"), 0f, 1f)
Textures add TextureSprite(get("entities", "clayPot$2Destroyed"), 1f, 2f)
Textures add TextureSprite(get("entities", "clayPot$3Destroyed"), 4f, 1f)
Textures add TextureSprite(get("entities", "clayPot$4Destroyed"), 2f, 1f)
Textures add AnimatedTextureSprite(getArray("entities", "standingTorch$1#1", "standingTorch$1#2", "standingTorch$1#3", ), true, 0.25f, LightSources(listOf(arrayListOf(
		Light(6f, 23f, Color.valueOf("f36422a2"), 12f, 512f),
	),arrayListOf(
		Light(6f, 23f, Color.valueOf("f36422a2"), 12f, 512f),
	),arrayListOf(
		Light(6f, 23f, Color.valueOf("f36422a2"), 12f, 512f),
	),)), 1f, 4f)
Textures add AnimatedTextureSprite(getArray("entities", "standingTorch$2#1", "standingTorch$2#2", "standingTorch$2#3", ), true, 0.25f, LightSources(listOf(arrayListOf(
		Light(6f, 21f, Color.valueOf("f36422a0"), 10f, 384f),
	),arrayListOf(
		Light(6f, 21f, Color.valueOf("f36422a0"), 10f, 384f),
	),arrayListOf(
		Light(6f, 21f, Color.valueOf("f36422a0"), 10f, 384f),
	),)), 1f, 4f)
Textures add AnimatedTextureSprite(getArray("entities", "torchFront#1", "torchFront#2", "torchFront#3", ), true, 0.25f, LightSources(listOf(arrayListOf(
		Light(2f, 7f, Color.valueOf("f3510783"), 6f, 128f),
	),arrayListOf(
		Light(2f, 7f, Color.valueOf("f3510783"), 6f, 128f),
	),arrayListOf(
		Light(2f, 7f, Color.valueOf("f3510783"), 6f, 128f),
	),)), 5f, 19f)
Textures add AnimatedTextureSprite(getArray("entities", "torchSide#1", "torchSide#2", "torchSide#3", ), true, 0.25f, LightSources(listOf(arrayListOf(
		Light(2f, 5f, Color.valueOf("f3510783"), 6f, 128f),
	),arrayListOf(
		Light(2f, 5f, Color.valueOf("f3510783"), 6f, 128f),
	),arrayListOf(
		Light(2f, 5f, Color.valueOf("f3510783"), 6f, 128f),
	),)), -1f, 3f)
Textures add TextureSprite(get("entities", "barrel"), 3f, 1f)
Textures add TextureSprite(get("entities", "barrelDestroyed"), 0f, 1f)
Textures add TextureSprite(get("entities", "crateBiggerDark"), 2f)
Textures add TextureSprite(get("entities", "crateBiggerDarkDestroyed"))
Textures add TextureSprite(get("entities", "crateSmall"), 3f)
Textures add TextureSprite(get("entities", "crateSmallDestroyed"))
Textures add TextureSprite(get("entities", "woodenChestMid"), 1f, 1f)
Textures add AnimatedTextureSprite(getArray("characters", "mouse_attack#1", "mouse_attack#2", "mouse_attack#3", "mouse_attack#4"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "mouse_death#1", "mouse_death#2", "mouse_death#3", "mouse_death#4"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "mouse_idle#1", "mouse_idle#1", "mouse_idle#1", "mouse_idle#1", "mouse_idle#2", "mouse_idle#3", "mouse_idle#4", "mouse_idle#5"), true, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "mouse_walk#1", "mouse_walk#2", "mouse_walk#3", "mouse_walk#4"), true, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "player_walk#1", "player_walk#2", "player_walk#3", "player_walk#4"), true, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "player_attack#1", "player_attack#2", "player_attack#3", "player_attack#4"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "player_idle#1", "player_idle#2"), true, 1.0f)
Textures add AnimatedTextureSprite(getArray("characters", "player_death#1", "player_death#2", "player_death#3", "player_death#4"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "Slime_attack#1", "Slime_attack#2", "Slime_attack#3", "Slime_attack#4", "Slime_attack#5", "Slime_attack#6"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "Slime_death#1", "Slime_death#2", "Slime_death#3", "Slime_death#4"), false, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "Slime_idle#1", "Slime_idle#1", "Slime_idle#1", "Slime_idle#2", "Slime_idle#3", "Slime_idle#4"), true, 0.25f)
Textures add AnimatedTextureSprite(getArray("characters", "Slime_walk#1", "Slime_walk#2", "Slime_walk#3", "Slime_walk#4", "Slime_walk#5", "Slime_walk#6", "Slime_walk#7", "Slime_walk#8"), true, 0.25f)
Textures add TextureSprite(get("items", "basicFireWand"), 3f, 3f)
Textures add TextureSprite(get("items", "basicPoisonWand"), 4f, 4f)
Textures add TextureSprite(get("items", "bigBow"), 1f, 1f)
Textures add TextureSprite(get("items", "book"), 2f)
Textures add TextureSprite(get("items", "brokenSword"), 4f, 3f)
Textures add TextureSprite(get("items", "cuffedLinenPants"), 3f, 3f)
Textures add TextureSprite(get("items", "curvedBow"), 5f, 2f)
Textures add TextureSprite(get("items", "dagger"), 4f, 4f)
Textures add TextureSprite(get("items", "extinguishedFireWand"), 4f, 4f)
Textures add TextureSprite(get("items", "fireStaff"), 1f, 1f)
Textures add TextureSprite(get("items", "gold1"), 5f, 6f)
Textures add TextureSprite(get("items", "gold2"), 3f, 4f)
Textures add TextureSprite(get("items", "gold3"), 2f, 2f)
Textures add TextureSprite(get("items", "gold4"), 2f, 2f)
Textures add TextureSprite(get("items", "gold5"), 5f, 5f)
Textures add TextureSprite(get("items", "gold6"), 2f, 4f)
Textures add TextureSprite(get("items", "gold7"), 1f, 3f)
Textures add TextureSprite(get("items", "gold8"), 2f, 2f)
Textures add TextureSprite(get("items", "knife"), 2f, 2f)
Textures add TextureSprite(get("items", "leatherBoots"), 0f, 3f)
Textures add TextureSprite(get("items", "leatherCap"), 2f, 4f)
Textures add TextureSprite(get("items", "leatherJacket"), 2f, 2f)
Textures add TextureSprite(get("items", "leatherSocks"), 1f, 6f)
Textures add TextureSprite(get("items", "linenShirt"), 2f, 3f)
Textures add TextureSprite(get("items", "meat"), 2f, 3f)
Textures add TextureSprite(get("items", "pocketKnife"), 5f, 4f)
Textures add TextureSprite(get("items", "reinforcedLeatherBoots"))
Textures add TextureSprite(get("items", "reinforcedLeatherCap"), 4f, 3f)
Textures add TextureSprite(get("items", "rippedPants"), 4f, 4f)
Textures add TextureSprite(get("items", "rockWand"), 4f, 4f)
Textures add TextureSprite(get("items", "scrollOfDefence"), 1f, 1f)
Textures add TextureSprite(get("items", "scrollOfFireball"), 1f, 1f)
Textures add TextureSprite(get("items", "scrollOfTeleportation"), 1f, 1f)
Textures add TextureSprite(get("items", "smallBackpack"), 4f, 3f)
Textures add TextureSprite(get("items", "smallBag"), 3f, 2f)
Textures add TextureSprite(get("items", "smallBow"), 5f, 2f)
Textures add TextureSprite(get("items", "smallHealingPotion"), 4f, 2f)
Textures add TextureSprite(get("items", "tornShirt"), 2f, 3f)
Textures add TextureSprite(get("items", "woodenFlipFlops"), 2f, 3f)
