package com.example.moneytracker.ui.usecase

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import javax.inject.Inject

class GetCurrentWeekUseCase @Inject constructor() {
    operator fun invoke(currentWeek: List<java.time.LocalDate>): List<LocalDate> {
        return currentWeek.map { it.toKotlinLocalDate() }
    }
}
