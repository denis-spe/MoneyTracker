package com.example.moneytracker.ui.usecase

import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetCurrentDateUseCase @Inject constructor() {
    operator fun invoke(
        currentWeek: List<java.time.LocalDate>,
        fallbackDate: LocalDate
    ): LocalDate {
        return if (currentWeek.isEmpty()) fallbackDate
        else {
            // Convert first date of current week to Kotlin LocalDate
            val firstDate = currentWeek.first()
            LocalDate(firstDate.year, firstDate.monthValue, firstDate.dayOfMonth)
        }
    }
}
