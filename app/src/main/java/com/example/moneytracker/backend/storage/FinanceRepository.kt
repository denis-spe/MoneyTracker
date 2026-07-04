package com.example.moneytracker.backend.storage

import com.example.moneytracker.ApplicationScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepository @Inject constructor(
    accountService: AccountServices,
    observeUserDataUseCase: ObserveUserDataUseCase,
    @ApplicationScope externalScope: CoroutineScope
) {
    val rawDatasetsFlow: StateFlow<HomeData> = observeUserDataUseCase(
        accountService.userState.map { it?.uid }
    )
        .flowOn(Dispatchers.Default)
        .stateIn(
            externalScope,
            SharingStarted.WhileSubscribed(5000),
            HomeData(datasetState = DatasetState.Loading)
        )
}
