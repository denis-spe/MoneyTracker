package com.example.moneytracker

import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DataStorageImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {

    private const val EMULATOR_IP = "192.168.43.53"

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        if (BuildConfig.DEBUG) {
            auth.useEmulator(EMULATOR_IP, 9090)
        }
        return auth
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()

        if (BuildConfig.DEBUG) {
            firestore.useEmulator(EMULATOR_IP, 8080)
        }

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(100 * 1024 * 1024) // 100 MB cache
                    .build()
            )
            .build()
        firestore.firestoreSettings = settings
        
        FirebaseFirestore.setLoggingEnabled(true)
        return firestore
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