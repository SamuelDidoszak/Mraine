package com.neutrino.game.entities.characters

import com.neutrino.GlobalData
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.*
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.systems.attack.attributes.Stats
import com.neutrino.game.entities.systems.attack.callables.LifestealCallable
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.entities.systems.attack.util.StatsEnum
import com.neutrino.game.entities.systems.skills.*
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.util.compareDelta

val Player = Character()
    .addAttribute(
        Stats(
        hpMax = 30f,
        mpMax = 10f,
        damageMin = 2f,
        damageMax = 4f,
        criticalChance = 0.05f,
        movementSpeed = 1.0,
        attackSpeed = 1.0,
        range = 1
    )
    )
    .addAttribute(Level())
    .addAttribute(PlayerAi())
    .addAttribute(Faction(FactionEnum.PLAYER))
    .addAttribute(Texture { position, random, textures ->
        textures.add(Textures.get("playerIdle").xy(0f, 5f))
    })
    .addAttribute(CharacterTags(CharacterTag.Lifesteal(0.1f)))
    .addAttribute(Inventory())
    .addAttribute(Equipment())
    .addAttribute(Skills())
    .also { it.id = 21370 }
    .also {
        attachCallables(it)
        addSkills(it)
        it.name = "Player"
    }

private fun attachCallables(entity: Entity) {
    entity.attach(object : VisionChangedCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            GlobalData.notifyObservers(GlobalDataType.PLAYERMOVED)
        }
    })
    entity.attach(object : StatsChangedCallable() {
        override fun call(entity: Entity, vararg data: Any?) {
            when (data[0]) {
                StatsEnum.HP ->
                    GlobalData.notifyObservers(GlobalDataType.PLAYERHP, (data[1] as Float).compareDelta(0f))
                StatsEnum.MP ->
                    GlobalData.notifyObservers(GlobalDataType.PLAYERMANA, (data[1] as Float).compareDelta(0f))
                else -> return
        } }
    })
    entity.attach(LifestealCallable())
}

private fun addSkills(entity: Entity) {
    val skillList = entity.get(Skills::class)!!

    skillList.addSkill(SkillBleed(entity))
    skillList.addSkill(SkillCripplingSpin(entity))
    skillList.addSkill(SkillTeleport(entity))
    skillList.addSkill(SkillTeleportToStairs(entity))
    skillList.addSkill(SkillTeleportToStairsDown(entity))
    skillList.addSkill(SkillMeteorite(entity))
    skillList.addSkill(SkillTeleportBackstab(entity))
    skillList.addSkill(SkillManaDrain(entity))
    skillList.addSkill(SkillShieldBash(entity))
    skillList.addSkill(SkillTwoshot(entity))
    skillList.addSkill(SkillGutAttack(entity))
}
