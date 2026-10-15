package com.employeedirectory.app.di

import com.employeedirectory.app.data.employeedirectory.remote.NetworkModule
import com.employeedirectory.app.data.employeedirectory.repository.EmployeeRepositoryImpl
import com.employeedirectory.app.domain.employeedirectory.repository.EmployeeRepository

/**
 * Minimal hand-written dependency container (a "poor man's DI"). A full DI framework
 * (Hilt/Koin) is intentionally avoided to keep the project easy to read for a junior-level
 * assignment; this single object is the only place that wires concrete implementations
 * together, so swapping in Hilt later only touches this file.
 */
class AppContainer {
    val employeeRepository: EmployeeRepository by lazy {
        EmployeeRepositoryImpl(NetworkModule.provideEmployeeApiService())
    }
}
