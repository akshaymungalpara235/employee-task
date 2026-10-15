package com.employeedirectory.app.domain.employeedirectory.model

/**
 * Pure domain representation of an employee. Age and salary are nullable because the
 * remote API is not guaranteed to provide every field for every record; the UI layer
 * decides how to present a missing value (e.g. "N/A") instead of crashing on it.
 */
data class Employee(
    val name: String,
    val age: Int?,
    val salary: Double?
)
