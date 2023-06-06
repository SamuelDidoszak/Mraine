package com.neutrino.game.domain.model.systems.event.wrappers

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.neutrino.game.domain.model.systems.event.Event
import com.neutrino.game.utility.serialization.HeaderSerializable

class OnOffEvent(
    @Transient
    override val event: Event
): EventWrapper(), HeaderSerializable {
    override fun serializeHeader(kryo: Kryo?, output: Output?) {
        kryo?.writeClassAndObject(output, event)
    }

    constructor(kryo: Kryo?, input: Input?): this(kryo!!.readClassAndObject(input) as Event)
}