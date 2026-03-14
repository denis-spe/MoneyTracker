// Bless be the name of LORD of hosts
package com.example.moneytracker.backend.alarmManager

import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.workManager.Work
import dagger.hilt.components.SingletonComponent

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {

    fun dataStorage(): DataStorage

    fun alarmManager(): AndroidAlarmManager

    fun useWorker(): Work
}