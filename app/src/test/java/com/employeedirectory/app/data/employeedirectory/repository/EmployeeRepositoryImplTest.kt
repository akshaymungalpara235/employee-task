package com.employeedirectory.app.data.employeedirectory.repository

import com.employeedirectory.app.data.employeedirectory.remote.EmployeeApiService
import com.employeedirectory.app.data.employeedirectory.remote.EmployeeDto
import com.employeedirectory.app.data.employeedirectory.remote.EmployeeListResponseDto
import com.employeedirectory.app.domain.common.DataResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/** Simple hand-written fake — avoids pulling in a mocking library for one interface. */
private class FakeEmployeeApiService(
    private val response: EmployeeListResponseDto? = null,
    private val error: Throwable? = null
) : EmployeeApiService {
    override suspend fun getEmployees(): EmployeeListResponseDto {
        error?.let { throw it }
        return response ?: EmployeeListResponseDto(employees = emptyList())
    }
}

class EmployeeRepositoryImplTest {

    @Test
    fun `successful response maps to domain employees`() = runBlocking {
        val api = FakeEmployeeApiService(
            response = EmployeeListResponseDto(
                employees = listOf(
                    EmployeeDto(name = "James", age = 27, salary = 17000.0),
                    EmployeeDto(name = "Robert", age = 27, salary = 25000.0)
                )
            )
        )
        val repository = EmployeeRepositoryImpl(api)

        val result = repository.getEmployees()

        assertTrue(result is DataResult.Success)
        assertEquals(2, (result as DataResult.Success).data.size)
    }

    @Test
    fun `invalid entries are filtered out without crashing`() = runBlocking {
        val api = FakeEmployeeApiService(
            response = EmployeeListResponseDto(
                employees = listOf(
                    EmployeeDto(name = "James", age = 27, salary = 17000.0),
                    EmployeeDto(name = null, age = null, salary = null)
                )
            )
        )
        val repository = EmployeeRepositoryImpl(api)

        val result = repository.getEmployees() as DataResult.Success
        assertEquals(1, result.data.size)
    }

    @Test
    fun `null employees field maps to empty success`() = runBlocking {
        val api = FakeEmployeeApiService(response = EmployeeListResponseDto(employees = null))
        val repository = EmployeeRepositoryImpl(api)

        val result = repository.getEmployees() as DataResult.Success
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `network failure maps to error result`() = runBlocking {
        val api = FakeEmployeeApiService(error = IOException("no network"))
        val repository = EmployeeRepositoryImpl(api)

        val result = repository.getEmployees()

        assertTrue(result is DataResult.Error)
        assertTrue((result as DataResult.Error).message.contains("internet", ignoreCase = true))
    }

    @Test
    fun `unexpected exception maps to generic error result`() = runBlocking {
        val api = FakeEmployeeApiService(error = IllegalStateException("boom"))
        val repository = EmployeeRepositoryImpl(api)

        val result = repository.getEmployees()

        assertTrue(result is DataResult.Error)
    }
}
