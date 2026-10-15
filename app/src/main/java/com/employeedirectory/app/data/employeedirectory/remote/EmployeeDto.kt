package com.employeedirectory.app.data.employeedirectory.remote

import com.google.gson.annotations.SerializedName

/**
 * Wire-format models mirroring https://aamras.com/dummy/EmployeeDetails.json exactly.
 * Every field is nullable: the API gives no schema guarantee, and a missing/malformed
 * field here must not crash Gson parsing or the app — it is handled by the mapper instead.
 */
data class EmployeeListResponseDto(
    @SerializedName("employees") val employees: List<EmployeeDto>?
)

data class EmployeeDto(
    @SerializedName("name") val name: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("salary") val salary: Double?
)
