package com.employeedirectory.app.presentation.common

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun `formats whole number with grouping and rupee symbol`() {
        assertEquals("₹17,000", CurrencyFormatter.format(17000.0))
    }

    @Test
    fun `formats large number with multiple grouping separators`() {
        assertEquals("₹1,250,000", CurrencyFormatter.format(1250000.0))
    }

    @Test
    fun `formats small number without separator`() {
        assertEquals("₹500", CurrencyFormatter.format(500.0))
    }

    @Test
    fun `null salary formats as N A placeholder`() {
        assertEquals("N/A", CurrencyFormatter.format(null))
    }

    @Test
    fun `fractional salary is rounded to whole currency unit`() {
        assertEquals("₹17,001", CurrencyFormatter.format(17000.6))
    }
}
