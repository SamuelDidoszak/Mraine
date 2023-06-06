package com.neutrino.game.utility.serialization

import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
import com.esotericsoftware.kryo.kryo5.io.Output
import com.esotericsoftware.kryo.kryo5.serializers.CompatibleFieldSerializer

open class HeaderFieldSerializer<T: HeaderSerializable>(kryo: Kryo?, type: Class<*>?) : CompatibleFieldSerializer<T>(kryo, type) {

    open override fun write(kryo: Kryo?, output: Output?, `object`: T) {
        `object`.serializeHeader(kryo, output)
        super.write(kryo, output, `object`)
    }

    override fun create(kryo: Kryo?, input: Input?, type: Class<out T>?): T {
        try {
            val constructor = type!!.getDeclaredConstructor(Kryo::class.java, Input::class.java)
            return constructor.newInstance(kryo, input) as T
        } catch (_: NoSuchMethodException) {
//            println("Can't find the kryo constructor")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val ctor = type!!.getDeclaredConstructor()
        ctor.setAccessible(true)
        return ctor.newInstance()
    }

    open override fun read(kryo: Kryo?, input: Input?, type: Class<out T>?): T {
        val obj = super.read(kryo, input, type)
        obj.readAfter(kryo, input)
        return obj
    }
}