package com.neutrino.game.entities.characters

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.attack.attributes.Stats
import com.neutrino.game.entities.systems.attack.callables.LifestealCallable
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.compareDelta

val Player = Character()
    .addAttribute(
        Stats(
        strength = 1f,
        intelligence = 10f,
        hpMax = 30f,
        mpMax = 10f,
        damageMin = 2f,
        damageMax = 4f,
        criticalChance = 0.05f,
        movementSpeed = 1.0,
        attackSpeed = 1.0,
        range = 1
    )
    )
    .addAttribute(Level())
    .addAttribute(Ai())
    .addAttribute(Faction(FactionEnum.PLAYER))
    .addAttribute(Texture { position, random, textures ->
        textures.add(Textures.get("player_idle"))
    })
    .addAttribute(CharacterTags(CharacterTag.Lifesteal(0.1f)))
    .addAttribute(Inventory())
    .addAttribute(Equipment())
    .also { it.id = 21370 }
    .also {
        attachCallables(it)
        it.name = "Player"
    }

private fun attachCallables(entity: Entity) {
    entity.attach(object : VisionChangedCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
        }
    })
    entity.attach(object : StatsChangedCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            when (data[0]) {
                StatsEnum.HP ->
                    GlobalData.notifyObservers(GlobalDataType.PLAYERHP, (data[1] as Float).compareDelta(0f))
                StatsEnum.MP ->
                    GlobalData.notifyObservers(GlobalDataType.PLAYERMANA, (data[1] as Float).compareDelta(0f))
                else -> return
        } }
    })
    entity.attach(LifestealCallable())
}
