package com.example.moneytracker.ui.usecase

import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetCurrentDateUseCase @Inject constructor() {
    operator fun invoke(
        currentWeek: List<LocalDate>,
        fallbackDate: LocalDate
    ): LocalDate {
        return if (currentWeek.isEmpty()) fallbackDate
        else {
            currentWeek.first()
        }
    }
}

