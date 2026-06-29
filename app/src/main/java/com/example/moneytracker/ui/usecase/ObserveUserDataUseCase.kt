package com.example.moneytracker.ui.usecase

import android.util.Log
import com.example.moneytracker.StartupTimer
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.Info
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
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
                combine(
                    dataStorage.getWholeDatasets(uid, {}, {})
                        .onStart { StartupTimer.mark("getWholeDatasets started") }
                        .catch { e ->
                            Log.e("ObserveUserDataUseCase", "datasets error", e)
                            emit(emptyList())
                        },
                    dataStorage.getInfo(uid)
                        .onStart { StartupTimer.mark("getInfo started") }
                        .catch { e ->
                            Log.e("ObserveUserDataUseCase", "info error", e)
                            emit(Info())
                        }
                ) { datasets, info ->
                    StartupTimer.mark("combine emitted datasets=${datasets.size}")
                    HomeData(
                        datasets = datasets,
                        info = info,
                        datasetState = DatasetState.Success
                    )
                }.onStart {
                    emit(HomeData(datasetState = DatasetState.Loading))
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
