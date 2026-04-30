package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.helper.isForToday
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetTodayFinanceUseCase @Inject constructor() {
    suspend operator fun invoke(financeList: List<Finance>): List<Finance> {
        var todayFinance = financeList
        withContext(kotlinx.coroutines.Dispatchers.Default) {
            todayFinance = financeList.filter { it.isForToday }
        }
        return todayFinance
    }
}
