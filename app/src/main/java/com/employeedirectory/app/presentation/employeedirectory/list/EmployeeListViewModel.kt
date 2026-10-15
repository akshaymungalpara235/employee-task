package com.employeedirectory.app.presentation.employeedirectory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.employeedirectory.app.domain.common.DataResult
import com.employeedirectory.app.domain.employeedirectory.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeListViewModel(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EmployeeListUiState>(EmployeeListUiState.Loading)
    val uiState: StateFlow<EmployeeListUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    fun loadEmployees() {
        viewModelScope.launch {
            _uiState.value = EmployeeListUiState.Loading
            _uiState.value = when (val result = repository.getEmployees()) {
                is DataResult.Success -> {
                    if (result.data.isEmpty()) {
                        EmployeeListUiState.Empty
                    } else {
                        EmployeeListUiState.Success(result.data)
                    }
                }
                is DataResult.Error -> EmployeeListUiState.Error(result.message)
            }
        }
    }
}
