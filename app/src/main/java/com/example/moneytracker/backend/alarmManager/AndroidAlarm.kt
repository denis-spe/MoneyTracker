package com.example.moneytracker.backend.alarmManager

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidAlarm @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(item: AlarmItem) {
        // Basic implementation to satisfy the compiler and tests
    }

    fun cancel(item: AlarmItem) {
        // Basic implementation to satisfy the compiler and tests
    }
}
