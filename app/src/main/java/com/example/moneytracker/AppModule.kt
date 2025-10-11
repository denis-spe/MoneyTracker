package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServiceImpl
import com.example.moneytracker.backend.auth.AccountServices
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAccountService(): AccountServices {
        return AccountServiceImpl(FirebaseAuth.getInstance())
    }

}