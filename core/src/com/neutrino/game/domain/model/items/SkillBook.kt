package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.systems.event.types.EventLearnSkill
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor



abstract class SkillBook(val skill: Skill): Item(), ItemType.USABLE {

    override val name: String = ColorUtils.getSkillTypeColor(skill.skillType).toTextraColor() + skill.name + " skill book"
    override var goldValueOg: Int = 0
    open override val itemTier: Int = 3

    override val useOn: UseOn = UseOn.SELF_ONLY
    override val hasRange: HasRange? = null

    override val eventWrappers: List<EventWrapper> = listOf(
        OnOffEvent(EventLearnSkill(skill::class))
    )

    override val textureNames: List<String> = listOf()

    override lateinit var texture: TextureAtlas.AtlasRegion

    fun getTexture(skillType: SkillType): TextureAtlas.AtlasRegion {
        return super.getTexture("book")
    }

    init {
        texture = getTexture(skill.skillType)
    }

}