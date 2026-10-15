package com.employeedirectory.app.data.employeedirectory.mapper

import com.employeedirectory.app.data.employeedirectory.remote.EmployeeDto
import com.employeedirectory.app.domain.employeedirectory.model.Employee

/**
 * Converts wire-format DTOs into domain models. An entry with a blank/missing name is
 * dropped — there is nothing meaningful to show or tap into — but a missing age or salary
 * is preserved as null so the UI can render a placeholder instead of the row disappearing.
 */
fun EmployeeDto.toDomainOrNull(): Employee? {
    val safeName = name?.trim()
    if (safeName.isNullOrEmpty()) return null
    return Employee(name = safeName, age = age, salary = salary)
}

fun List<EmployeeDto>.toDomainEmployees(): List<Employee> = mapNotNull { it.toDomainOrNull() }
