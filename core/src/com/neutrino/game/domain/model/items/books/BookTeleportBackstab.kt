package com.neutrino.game.domain.model.items.books

import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.SkillBook
import com.neutrino.game.domain.model.systems.skills.SkillTeleportBackstab

class BookTeleportBackstab: SkillBook(SkillTeleportBackstab(Player)) {
    override val name: String = "Backstab skill book"
    override val description: String = skill.description

    override var goldValue: Int = 20
    override var realValue: Int = 25
}