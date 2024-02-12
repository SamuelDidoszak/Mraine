package com.neutrino.game.entities.characters

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.textures.Textures

val Player = Entity()
    .addAttribute(
        Stats(
        strength = 1f,
        intelligence = 10f,
        hpMax = 30f,
        mpMax = 10f,
        damageMin = 2f,
        damageMax = 4f,
        criticalChance = 0.05f,
        criticalDamage = 2f,
        movementSpeed = 1.0,
        attackSpeed = 1.0,
        range = 1
    ))
    .addAttribute(Level())
    .addAttribute(Ai())
    .addAttribute(Faction(FactionEnum.PLAYER))
    .addAttribute(Texture { position, random, textures ->
        textures.add(Textures.get("player_idle"))
    })
    .addAttribute(CharacterTags())
    .also { it.id = 0 }
    .also { attachCallables(it) }

private fun attachCallables(entity: Entity) {
    entity.attach(object : VisionChangedCallable() {
        override fun call(entity: Entity) {
            GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
        }
    })
}
