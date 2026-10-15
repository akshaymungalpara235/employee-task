package com.employeedirectory.app.presentation.employeedirectory.list

import com.employeedirectory.app.domain.common.DataResult
import com.employeedirectory.app.domain.employeedirectory.model.Employee
import com.employeedirectory.app.domain.employeedirectory.repository.EmployeeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Returns after a (virtual-time) suspension point rather than instantly, so tests can
 * observe the Loading state before the result arrives — a real network call would behave
 * the same way, but an instantly-returning fake never actually suspends.
 */
private class FakeEmployeeRepository(
    private val result: DataResult<List<Employee>>
) : EmployeeRepository {
    override suspend fun getEmployees(): DataResult<List<Employee>> {
        delay(1)
        return result
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class EmployeeListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful load exposes employees in success state`() = runTest {
        val employees = listOf(Employee("James", 27, 17000.0))
        val viewModel = EmployeeListViewModel(FakeEmployeeRepository(DataResult.Success(employees)))

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is EmployeeListUiState.Success)
        assertEquals(employees, (state as EmployeeListUiState.Success).employees)
    }

    @Test
    fun `empty employee list exposes empty state`() = runTest {
        val viewModel = EmployeeListViewModel(FakeEmployeeRepository(DataResult.Success(emptyList())))

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is EmployeeListUiState.Empty)
    }

    @Test
    fun `repository error exposes error state with message`() = runTest {
        val viewModel = EmployeeListViewModel(
            FakeEmployeeRepository(DataResult.Error("No internet connection. Please check your network and try again."))
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is EmployeeListUiState.Error)
        assertEquals("No internet connection. Please check your network and try again.", (state as EmployeeListUiState.Error).message)
    }

    @Test
    fun `loadEmployees resets state to loading before new result arrives`() = runTest {
        val viewModel = EmployeeListViewModel(FakeEmployeeRepository(DataResult.Success(emptyList())))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadEmployees()
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.uiState.value is EmployeeListUiState.Loading)

        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value is EmployeeListUiState.Empty)
    }
}
