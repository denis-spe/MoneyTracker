package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.isForToday
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTodayFinanceUseCase @Inject constructor() {
    suspend operator fun invoke(financeEntityList: List<FinanceEntity>): List<FinanceEntity> {
        var todayFinance = financeEntityList
        withContext(kotlinx.coroutines.Dispatchers.Default) {
            todayFinance = financeEntityList.filter { it.isForToday }
        }
        return todayFinance
    }
}
