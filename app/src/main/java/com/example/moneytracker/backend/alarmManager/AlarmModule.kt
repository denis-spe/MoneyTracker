package com.example.moneytracker.backend.alarmManager

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindAlarmManager(
        androidAlarm: AndroidAlarm
    ): AndroidAlarmManager
}