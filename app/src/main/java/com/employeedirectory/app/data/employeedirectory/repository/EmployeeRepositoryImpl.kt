package com.employeedirectory.app.data.employeedirectory.repository

import com.employeedirectory.app.data.employeedirectory.mapper.toDomainEmployees
import com.employeedirectory.app.data.employeedirectory.remote.EmployeeApiService
import com.employeedirectory.app.domain.common.DataResult
import com.employeedirectory.app.domain.employeedirectory.model.Employee
import com.employeedirectory.app.domain.employeedirectory.repository.EmployeeRepository
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class EmployeeRepositoryImpl(
    private val apiService: EmployeeApiService
) : EmployeeRepository {

    override suspend fun getEmployees(): DataResult<List<Employee>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmployees()
                val employees = response.employees.orEmpty().toDomainEmployees()
                DataResult.Success(employees)
            } catch (e: IOException) {
                // No connectivity, DNS failure, timeout, etc.
                DataResult.Error("No internet connection. Please check your network and try again.", e)
            } catch (e: HttpException) {
                DataResult.Error("Server error (code ${e.code()}). Please try again later.", e)
            } catch (e: JsonSyntaxException) {
                DataResult.Error("Received an unreadable response from the server.", e)
            } catch (e: Exception) {
                DataResult.Error("Something went wrong while loading employees.", e)
            }
        }
}
