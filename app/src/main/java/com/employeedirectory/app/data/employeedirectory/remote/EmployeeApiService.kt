package com.employeedirectory.app.data.employeedirectory.remote

import retrofit2.http.GET

interface EmployeeApiService {
    @GET("dummy/EmployeeDetails.json")
    suspend fun getEmployees(): EmployeeListResponseDto
}
