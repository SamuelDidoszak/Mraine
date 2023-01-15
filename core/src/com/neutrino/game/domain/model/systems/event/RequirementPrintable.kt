package com.neutrino.game.domain.model.systems.event

class RequirementPrintable(
    override val data: MutableMap<String, Data<*>> = mutableMapOf()
): DataMap {
    private val requirementList: ArrayList<() -> Boolean> = ArrayList()
    private val printableForm: ArrayList<String> = ArrayList()

    fun add(printableVersion: String, check: () -> Boolean): RequirementPrintable {
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

    fun print(highlightUnmet: Boolean = false, whitespaceAmount: Int): String {
        var printable: String = ""

        for (i in 0 until printableForm.size) {
            printable = printable.plus(
                if (highlightUnmet)
                    getRequirementColor(i)
                else
                    ""
                .plus("${printableForm[i]}${getWhitespace(whitespaceAmount)}")
            )
        }
        return printable
    }

    fun getPrintable(highlightUnmet: Boolean = false): List<String> {
        if (!highlightUnmet)
            return printableForm

        val highlightedPrintable = ArrayList<String>()
        for (i in 0 until printableForm.size) {
            highlightedPrintable.add(getRequirementColor(i) + printableForm[i])
        }
        return highlightedPrintable
    }

    private fun getRequirementColor(i: Int): String {
        return if (requirementList[i]()) "[Green]" else "[Red]"
    }

    private fun getWhitespace(amount: Int): String {
        var whitespace = ""
        for (i in 0 until amount) {
            whitespace = whitespace.plus("\n")
        }
        return whitespace
    }
}