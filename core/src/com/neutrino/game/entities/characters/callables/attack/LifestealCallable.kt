package com.neutrino.game.entities.characters.callables.attack

import com.neutrino.game.entities.Entity

class LifestealCallable: AttackedAfterCallable() {

    override fun call(entity: Entity, vararg data: Any?): Boolean {
            // TODO ECS Events
//            val heal = CharacterEvent(attacker.entity,
//                TimedEvent(1.0,
//                    EventHeal(damage * attacker.entity.get(CharacterTags::class)?.getTag(Lifesteal::class)!!.power)
//                ), Turn.turn)
//            EventDispatcher.dispatchEvent(heal)
        return true
    }
}