package com.neutrino.game.entities.characters

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.FactionEnum

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
    .addAttribute(CharacterTags())
