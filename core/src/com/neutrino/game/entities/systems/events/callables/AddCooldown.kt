package com.neutrino.game.entities.systems.events.callables

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.items.attributes.usable.UseEvents
import com.neutrino.game.entities.items.callables.UsedCallable
import com.neutrino.game.entities.systems.events.Cooldown
import com.neutrino.game.entities.systems.events.Event
import com.neutrino.game.entities.systems.events.Events
import kotlin.reflect.KClass

class AddCooldown(
    val type: Cooldown.Type,
    private val length: Double? = null
): UsedCallable() {
    /** Call this constructor when using an item should cause a cooldown */
    constructor(type: Cooldown.Type, useEventClass: KClass<out Event>): this(type) { this.useEventClass = useEventClass}

    private var useEventClass: KClass<out Event>? = null

    override fun call(entity: Entity, vararg data: Any?): Boolean {
        val cooldown = Cooldown(data[0] as Entity, type,
            length ?: entity.get(UseEvents::class)!!.get(useEventClass!!)!!.getEventLength())
        Events.addEvent(data[0] as Entity, cooldown.asTimedEvent())
        return true
    }
}