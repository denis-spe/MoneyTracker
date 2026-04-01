package com.example.moneytracker.backend.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val ROUTINE_DATASTORE_NAME = "routine_cache"
private val Context.routineDataStore: DataStore<Preferences> by preferencesDataStore(name = ROUTINE_DATASTORE_NAME)

@Singleton
class RoutineDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.routineDataStore

    // Keys for storing routine updates
    private fun dateTimeKey(datasetId: String) = stringPreferencesKey("datetime_$datasetId")
    private fun deadlineKey(datasetId: String) = stringPreferencesKey("deadline_$datasetId")
    private fun triggerMillisKey(datasetId: String) =
        longPreferencesKey("trigger_millis_$datasetId")

    // Store routine update locally for fast sync
    suspend fun cacheRoutineUpdate(
        datasetId: String,
        newDateTime: String,  // ISO format
        newDeadline: String,  // ISO format
        triggerMillis: Long
    ) {
        dataStore.edit { preferences ->
            preferences[dateTimeKey(datasetId)] = newDateTime
            preferences[deadlineKey(datasetId)] = newDeadline
            preferences[triggerMillisKey(datasetId)] = triggerMillis
        }
    }

    // Get cached routine update
    fun getRoutineUpdate(datasetId: String): Flow<RoutineUpdate?> =
        dataStore.data.map { preferences ->
            val dateTime = preferences[dateTimeKey(datasetId)]
            val deadline = preferences[deadlineKey(datasetId)]
            val triggerMillis = preferences[triggerMillisKey(datasetId)]

            if (dateTime != null && deadline != null && triggerMillis != null) {
                RoutineUpdate(dateTime, deadline, triggerMillis)
            } else {
                null
            }
        }

    // Clear cached update after successful Firestore sync
    suspend fun clearRoutineUpdate(datasetId: String) {
        dataStore.edit { preferences ->
            preferences.remove(dateTimeKey(datasetId))
            preferences.remove(deadlineKey(datasetId))
            preferences.remove(triggerMillisKey(datasetId))
        }
    }
}

data class RoutineUpdate(
    val dateTime: String,
    val deadline: String,
    val triggerMillis: Long
)
