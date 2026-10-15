package com.employeedirectory.app

import android.app.Application
import com.employeedirectory.app.di.AppContainer

class EmployeeDirectoryApp : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
