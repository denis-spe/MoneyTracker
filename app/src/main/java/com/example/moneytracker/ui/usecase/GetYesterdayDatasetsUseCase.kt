package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.helper.isCreatedAtEqualTo
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

class GetYesterdayFinanceUseCase @Inject constructor() {
    operator fun invoke(financeList: List<Finance>): List<Finance> {
        val yesterday = LocalDateTime.now()
            .date
            .minus(1, DateTimeUnit.DAY)

        return financeList.filter { it.isCreatedAtEqualTo(yesterday) }
    }
}
