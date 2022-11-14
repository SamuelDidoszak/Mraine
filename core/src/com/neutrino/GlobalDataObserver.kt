package com.neutrino

interface GlobalDataObserver {
    val dataType: GlobalDataType
    /** Returns true if the data should be removed after interpretation */
    fun update(data: Any? = null): Boolean
}