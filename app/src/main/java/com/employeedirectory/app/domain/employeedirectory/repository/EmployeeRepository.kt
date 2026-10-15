package com.employeedirectory.app.domain.employeedirectory.repository

import com.employeedirectory.app.domain.common.DataResult
import com.employeedirectory.app.domain.employeedirectory.model.Employee

/**
 * Domain-facing contract. The presentation layer depends on this interface only, never on
 * the concrete Retrofit/Gson implementation in the data layer.
 */
interface EmployeeRepository {
    suspend fun getEmployees(): DataResult<List<Employee>>
}
