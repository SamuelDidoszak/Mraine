package com.neutrino.game.domain.model.characters.utility

enum class CharacterAlignment(val enemies: HashSet<CharacterAlignment>) {
    PLAYER(hashSetOf()),
    ENEMY(hashSetOf(PLAYER)),
    NEUTRAL(hashSetOf()),
    FRIENDLY(hashSetOf(ENEMY)),
    FAMILIAR(hashSetOf(ENEMY))
}