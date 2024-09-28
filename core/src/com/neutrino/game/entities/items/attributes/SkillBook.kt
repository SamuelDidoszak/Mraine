package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Skills
import com.neutrino.game.entities.map_entities.attributes.PickUp
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.add
import kotlin.reflect.KClass

class SkillBook(
    val skill: KClass<out Skill>,
    val textureName: String,
    val rank: Int
): Attribute() {

    override fun onEntityAttached() {
        entity.addAttribute(Texture { position, random, textures -> run {
            textures add Textures.get(textureName)
        }})
        entity.addAttribute(GoldValue(rankGoldMap[rank - 1]))
        entity.addAttribute(Amount(maxStack = 1))
        entity.addAttribute(PickUp())
    }

    fun learn(entity: Entity) {
        if (entity hasNot Skills::class)
            entity.addAttribute(Skills())
        entity.get(Skills::class)!!.addSkill(skill)
    }

    private companion object {
        val rankGoldMap = listOf(
            20,
            55,
            150,
            500
        )
    }
}