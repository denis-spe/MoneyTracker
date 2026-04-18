package com.example.moneytracker.ui.usecase

import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetCurrentWeekUseCase @Inject constructor() {
    operator fun invoke(currentWeek: List<LocalDate>): List<LocalDate> {
        return currentWeek
    }
}

