package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DataStorageImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideDataStorage(firestore: FirebaseFirestore): DataStorage {
        return DataStorageImpl(firestore)
    }



    @Singleton
    @Provides
    fun provideAccountService(auth: FirebaseAuth): AccountServices {
        return AccountServicesImpl(auth)
    }

}