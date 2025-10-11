package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.Mockito
import javax.inject.Singleton


@Module
// The component it should install into and the module to replace
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseAuthModule::class]
)
object TestFirebaseAuthModule {
    @Provides
    @Singleton
    fun provideTestFirebaseAuth(): FirebaseAuth {
        // Return a mock FirebaseAuth instance using Mockito or similar framework
        return Mockito.mock(FirebaseAuth::class.java)
    }

    @Provides
    @Singleton
    fun provideAccountService(auth: FirebaseAuth): AccountServices {
        // Return fake service that doesn't call Firebase
        return AccountServicesImpl(auth)
    }

}