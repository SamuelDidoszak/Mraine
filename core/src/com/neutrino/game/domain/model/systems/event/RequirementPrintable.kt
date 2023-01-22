package com.neutrino.game.domain.model.systems.event

import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.graphics.utility.ColorUtils.toHexaDecimal

class RequirementPrintable(
    override val data: MutableMap<String, Data<*>> = mutableMapOf()
): DataMap {
    private val requirementList: ArrayList<() -> Boolean> = ArrayList()
    private val printableForm: ArrayList<PrintableReq> = ArrayList()

    fun add(printableVersion: PrintableReq, check: () -> Boolean): RequirementPrintable {
        requirementList.add(check)
        printableForm.add(printableVersion)
        return this
    }

    fun checkAll(): Boolean {
        if (!isDataSet())
            return false

        for (requirement in requirementList) {
            if (!requirement())
                return false
        }
        return true
    }

    fun getPrintable(highlightUnmet: Boolean = false): List<Pair<String, String>> {
        if (!highlightUnmet)
            return printableForm.map { Pair(it.requirementName, it.func.invoke().toString() + " / " + it.value.toString()) }

        val highlightedPrintable = ArrayList<Pair<String, String>>()
        for (i in 0 until printableForm.size) {
            highlightedPrintable.add(Pair(
                printableForm[i].requirementName,
                getRequirementColor(i) + printableForm[i].func.invoke().toString() + " / " + printableForm[i].value
            ))
        }
        return highlightedPrintable
    }

    private companion object {
        val metColor = "[${ColorUtils.REQ_MET.toHexaDecimal()}]"
        val unmetColor = "[${ColorUtils.REQ_UNMET.toHexaDecimal()}]"
    }

    private fun getRequirementColor(i: Int): String {
        return if (requirementList[i]()) metColor else unmetColor
    }

    data class PrintableReq(
        val requirementName: String,
        val value: Any?,
        val func: () -> Any?
    )
}