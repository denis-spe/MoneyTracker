package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DataStorageImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton


@Module
// The component it should install into and the module to replace
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseAuthModule::class]
)
object TestFirebaseAuthModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideDataStorage(firestore: FirebaseFirestore): DataStorage {
        return DataStorageImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideAccountService(): AccountServices {
        // Return fake service that doesn't call Firebase
        return FakeAccountServices()
    }

}