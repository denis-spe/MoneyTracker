// Glory be to the name of the LORD of hosts and our LORD JESUS
package com.example.moneytracker.backend.workers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {
    @Binds
    abstract fun bindWorkers(
        workers: Workers
    ): WorkerInf
}