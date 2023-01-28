package com.neutrino.game.domain.model.items.books

import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.SkillBook
import com.neutrino.game.domain.model.systems.skills.SkillTwoshot

class BookSkillTwoshot: SkillBook(SkillTwoshot(Player)) {
    override val name: String = "Twoshot skill book"
    override val description: String = skill.description

    override var goldValue: Int = 20
    override var realValue: Int = 25
}