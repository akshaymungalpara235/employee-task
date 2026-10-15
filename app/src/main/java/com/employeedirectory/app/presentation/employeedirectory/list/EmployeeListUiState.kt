package com.employeedirectory.app.presentation.employeedirectory.list

import com.employeedirectory.app.domain.employeedirectory.model.Employee

sealed class EmployeeListUiState {
    data object Loading : EmployeeListUiState()
    data class Success(val employees: List<Employee>) : EmployeeListUiState()
    data object Empty : EmployeeListUiState()
    data class Error(val message: String) : EmployeeListUiState()
}
