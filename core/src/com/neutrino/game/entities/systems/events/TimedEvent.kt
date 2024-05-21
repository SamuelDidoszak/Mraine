package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.util.compareDelta
import com.neutrino.game.util.roundOneDecimal
import kotlin.math.absoluteValue

class TimedEvent(
    val event: Event,
    var refreshTime: Double,
    var executions: Int,
    val startDelay: Double = 0.0,
    var turn: Double? = null
) {
    constructor(event: Event, infinite: Boolean = false, startDelay: Double = 0.0, startTurn: Double? = null):
            this(event, if (!infinite) 0.0 else Double.MAX_VALUE, 1, startDelay, startTurn)
    constructor(event: Event, timedData: TimedData, startDelay: Double = 0.0, startTurn: Double? = null):
            this(event, timedData.refreshTime, timedData.executions, startDelay, startTurn)

    fun getEventLength(): Double = (refreshTime * executions).roundOneDecimal()
    fun turnsRemaining(): Double = (Turn.turn - turn!!).absoluteValue
    val name: String
        get() = event::class.simpleName!!

    class TimedData(
        var power: Float,
        var refreshTime: Double,
        var executions: Int,
        var totalTurns: Double
    ) {
        constructor(power: Float, refreshTime: Double, executions: Int): this(power, refreshTime, executions, (refreshTime * executions).roundOneDecimal())
        constructor(power: Float, refreshTime: Double, totalTurns: Double): this(power, refreshTime, (totalTurns / refreshTime).toInt(), totalTurns)
        constructor(power: Float, executions: Int, totalTurns: Double): this(power, totalTurns / executions, executions, totalTurns)

        fun getEventPower(): Float = power / executions

        fun toTimedEvent(event: (TimedData) -> Event): TimedEvent = TimedEvent(event.invoke(this), this)
    }

    fun printable(other: TimedEvent?): String {
        val otherEvent = if (other != null && other.event::class == event::class) other.event else null
        return event.printable(otherEvent) + " ${PrintableInfo.baseColor}" + if (executions == 1) "instantly." else
            "per $refreshTime turn" + (if (refreshTime.compareDelta(1.0) == 1) "s " else " ") +
            "$executions times"
    }
}