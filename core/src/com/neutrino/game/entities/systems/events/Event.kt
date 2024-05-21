package com.neutrino.game.entities.systems.events

interface Event {
    fun apply()
    fun stop() {}

    fun printable(other: Event?): String = ""
}