package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.alarmManager.AndroidAlarm
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val androidAlarm: AndroidAlarm,
) {
    operator fun invoke(
        alarmItem: AlarmItem
    ) {
        androidAlarm.schedule(alarmItem)
    }
}
