package com.example.moneytracker.ui.homeScreen.allScreen

import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

data class AllUiState(
    val dates: List<LocalDate> = emptyList(),
    val currentWeek: List<LocalDate> = emptyList(),
    val date: LocalDate = LocalDate.now(),
    val selectedTabIndex: Int = 1
)
