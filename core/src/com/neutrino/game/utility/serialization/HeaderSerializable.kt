package com.neutrino.game.utility.serialization

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output

interface HeaderSerializable {

    fun serializeHeader(kryo: Kryo?, output: Output?) {

    }

    fun readAfter(kryo: Kryo?, input: Input?) {

    }
}