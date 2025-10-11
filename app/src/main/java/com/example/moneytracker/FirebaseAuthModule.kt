package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideAccountService(auth: FirebaseAuth): AccountServices {
        return AccountServicesImpl(auth)
    }

}