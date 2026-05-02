package com.example.moneytracker.ui.usecase

import android.util.Log
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.FirestoreMigration
import com.example.moneytracker.backend.storage.Info
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveUserDataUseCase @Inject constructor(
    private val dataStorage: DataStorage
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userIdFlow: Flow<String?>): Flow<HomeData> {
        return userIdFlow.flatMapLatest { uid ->
            if (uid == null) {
                flowOf(
                    HomeData(
                        datasets = emptyList(),
                        datasetState = DatasetState.Loading
                    )
                )
            } else {
                try {
                    // Trigger migration and ensure IDs
                    FirestoreMigration.migrateUserDatasets(dataStorage.db, uid)
                    dataStorage.ensureDatasetIds(uid)
                } catch (e: Exception) {
                    Log.e("ObserveUserDataUseCase", "Migration or ensureDatasetIds failed", e)
                }

                combine(
                    dataStorage.getWholeDatasets(uid, {}, {})
                        .catch { e ->
                            Log.e("ObserveUserDataUseCase", "datasets error", e)
                            emit(emptyList())
                        },
                    dataStorage.getInfo(uid)
                        .catch { e ->
                            Log.e("ObserveUserDataUseCase", "info error", e)
                            emit(Info()) // ✅ FIXED
                        }
                ) { datasets, info ->
                    HomeData(
                        datasets = datasets,
                        info = info,
                        datasetState = DatasetState.Success
                    )
                }.catch { e ->
                    emit(
                        HomeData(
                            datasetState = DatasetState.Error(e.message),
                            error = e.message
                        )
                    )
                }
            }
        }
    }
}