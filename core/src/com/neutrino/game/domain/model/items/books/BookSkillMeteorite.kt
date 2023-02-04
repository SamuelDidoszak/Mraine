package com.neutrino.game.domain.model.items.books

import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.SkillBook
import com.neutrino.game.domain.model.systems.skills.SkillMeteorite
import kotlinx.serialization.Serializable

@Serializable
class BookSkillMeteorite: SkillBook(SkillMeteorite(Player)) {
    override val description: String = skill.description

    override var goldValue: Int = 20
    override var realValue: Int = 25
}