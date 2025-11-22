package com.example.moneytracker

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        // instructs the test process to use HiltTestApplication
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}