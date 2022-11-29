package com.neutrino.game.domain.model.event.types

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.HpBar
import com.neutrino.game.domain.model.event.Event

class EventHeal(
    val power: Float
): Event<Character> {
    override lateinit var data: Character
    override var dataAttached: Boolean = false

    override fun start() {
        checkData()

        val character = data
        character.hp += power
        if (character.hp > character.hpMax)
            character.hp = character.hpMax
        character.findActor<HpBar>("hpBar").update(character.hp)
    }
}