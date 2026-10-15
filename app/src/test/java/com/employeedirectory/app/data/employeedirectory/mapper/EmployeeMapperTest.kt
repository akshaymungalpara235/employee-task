package com.employeedirectory.app.data.employeedirectory.mapper

import com.employeedirectory.app.data.employeedirectory.remote.EmployeeDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EmployeeMapperTest {

    @Test
    fun `complete dto maps to domain employee`() {
        val dto = EmployeeDto(name = "James", age = 27, salary = 17000.0)

        val employee = dto.toDomainOrNull()

        assertEquals("James", employee?.name)
        assertEquals(27, employee?.age)
        assertEquals(17000.0, employee?.salary)
    }

    @Test
    fun `dto with blank name is dropped`() {
        val dto = EmployeeDto(name = "   ", age = 27, salary = 17000.0)

        assertNull(dto.toDomainOrNull())
    }

    @Test
    fun `dto with null name is dropped`() {
        val dto = EmployeeDto(name = null, age = 27, salary = 17000.0)

        assertNull(dto.toDomainOrNull())
    }

    @Test
    fun `dto with missing age and salary still maps using name`() {
        val dto = EmployeeDto(name = "Robert", age = null, salary = null)

        val employee = dto.toDomainOrNull()

        assertEquals("Robert", employee?.name)
        assertNull(employee?.age)
        assertNull(employee?.salary)
    }

    @Test
    fun `name is trimmed`() {
        val dto = EmployeeDto(name = "  Emma  ", age = 30, salary = 45000.0)

        assertEquals("Emma", dto.toDomainOrNull()?.name)
    }

    @Test
    fun `list mapping drops only invalid entries and keeps valid order`() {
        val dtos = listOf(
            EmployeeDto(name = "James", age = 27, salary = 17000.0),
            EmployeeDto(name = null, age = 30, salary = 20000.0),
            EmployeeDto(name = "Robert", age = 27, salary = 25000.0)
        )

        val employees = dtos.toDomainEmployees()

        assertEquals(2, employees.size)
        assertEquals("James", employees[0].name)
        assertEquals("Robert", employees[1].name)
    }

    @Test
    fun `empty list maps to empty list`() {
        assertEquals(0, emptyList<EmployeeDto>().toDomainEmployees().size)
    }
}
