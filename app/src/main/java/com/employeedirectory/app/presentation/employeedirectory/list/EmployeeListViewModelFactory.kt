package com.employeedirectory.app.presentation.employeedirectory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.employeedirectory.app.domain.employeedirectory.repository.EmployeeRepository

class EmployeeListViewModelFactory(
    private val repository: EmployeeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(EmployeeListViewModel::class.java)) {
            "Unknown ViewModel class: $modelClass"
        }
        return EmployeeListViewModel(repository) as T
    }
}
