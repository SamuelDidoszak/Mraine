package com.neutrino.game.entities.systems.events

import com.neutrino.game.util.roundOneDecimal

class TimedEvent(
    val event: Event,
    val refreshTime: Double,
    var executions: Int,
    val startDelay: Double = 0.0,
    var turn: Double? = null
) {
    constructor(event: Event, infinite: Boolean = false, startDelay: Double = 0.0, startTurn: Double? = null):
            this(event, if (!infinite) 0.0 else Double.MAX_VALUE, 1, startDelay, startTurn)
    constructor(event: Event, timedData: TimedData, startDelay: Double = 0.0, startTurn: Double? = null):
            this(event, timedData.refreshTime, timedData.executions, startDelay, startTurn)

    fun getEventLength(): Double = (refreshTime * executions).roundOneDecimal()

    class TimedData(
        val power: Float,
        val refreshTime: Double,
        val executions: Int,
        val totalTurns: Double
    ) {
        constructor(power: Float, refreshTime: Double, executions: Int): this(power, refreshTime, executions, (refreshTime * executions).roundOneDecimal())
        constructor(power: Float, refreshTime: Double, totalTurns: Double): this(power, refreshTime, (totalTurns / refreshTime).toInt(), totalTurns)
        constructor(power: Float, executions: Int, totalTurns: Double): this(power, totalTurns / executions, executions, totalTurns)

        fun getEventPower(): Float = power / executions
    }
}