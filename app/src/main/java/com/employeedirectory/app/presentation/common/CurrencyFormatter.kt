package com.employeedirectory.app.presentation.common

import java.text.NumberFormat
import java.util.Locale

/**
 * The API returns salary as a plain number with no currency unit, so the rupee symbol here
 * is a presentation choice, not something the API specifies. Changing the displayed
 * currency only requires editing CURRENCY_SYMBOL below.
 */
object CurrencyFormatter {

    private const val CURRENCY_SYMBOL = "₹" // ₹

    private val groupingFormat: NumberFormat = NumberFormat.getNumberInstance(Locale.US).apply {
        isGroupingUsed = true
        maximumFractionDigits = 0
    }

    fun format(salary: Double?): String {
        if (salary == null) return "N/A"
        return "$CURRENCY_SYMBOL${groupingFormat.format(salary)}"
    }
}
