package com.neutrino.game.entities.characters.attributes.util

enum class FactionEnum(val enemies: HashSet<FactionEnum>) {
    PLAYER(hashSetOf()),
    ENEMY(hashSetOf(PLAYER)),
    NEUTRAL(hashSetOf()),
    FRIENDLY(hashSetOf(ENEMY)),
    FAMILIAR(hashSetOf(ENEMY))
}