package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.GetCurrentDateUseCase
import com.example.moneytracker.ui.usecase.GetCurrentWeekUseCase
import com.example.moneytracker.ui.usecase.GetProfessionalSummaryUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.ProfessionalSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

data class AllScreenData(
    val uiState: AllUiState = AllUiState(),
    val groupedWeeklyData: DataState<List<Pair<LocalDate, List<DataSettlement>>>> = DataState.Loading,
    val professionalSummary: DataState<ProfessionalSummary> = DataState.Loading,
    val activityCounts: DataState<Map<LocalDate, Int>> = DataState.Loading,
    val currentWeekDerived: List<LocalDate> = emptyList(),
    val currentDateDerived: LocalDate = LocalDate.now()
)

@HiltViewModel
class AllViewModel @Inject constructor(
    financeRepository: FinanceRepository,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentWeekUseCase: GetCurrentWeekUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getProfessionalSummaryUseCase: GetProfessionalSummaryUseCase
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val _uiState = MutableStateFlow(AllUiState())
    val uiState: StateFlow<AllUiState> = _uiState.asStateFlow()

    private val datesFlow = uiState
        .map { it.dates }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

    private val currentWeekFlow = uiState
        .map { it.currentWeek }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

    val groupedWeeklyData: StateFlow<DataState<List<Pair<LocalDate, List<DataSettlement>>>>> =
        combine(
            rawDatasetsFlow,
            datesFlow
        ) { homeData, dates ->
            homeData.toDataState { datasets ->
                getWeeklyDataUseCase(datasets, dates)
                    .groupBy { it.createdAt.toLocalDateTimeUtc().date }
                    .toList()
                    .sortedByDescending { it.first }
            }
        }
            .flowOn(Dispatchers.Default)
            .distinctUntilChanged()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STATE_TIMEOUT),
                DataState.Loading
            )

    val professionalSummary: StateFlow<DataState<ProfessionalSummary>> = combine(
        rawDatasetsFlow,
        datesFlow
    ) { homeData, dates ->
        homeData.toDataState { datasets ->
            getProfessionalSummaryUseCase(getWeeklyDataUseCase(datasets, dates))
        }
    }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val activityCounts: StateFlow<DataState<Map<LocalDate, Int>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                val map = mutableMapOf<LocalDate, Int>()
                for (entity in datasets) {
                    val date = entity.createdAt.toLocalDateTimeUtc().date
                    map[date] = (map[date] ?: 0) + 1

                    val settlements = when (entity) {
                        is FinanceEntity.Goal -> entity.settlement
                        is FinanceEntity.Liability -> entity.settlement
                        else -> emptyList()
                    }
                    for (s in settlements) {
                        val sDate = s.dateTime.toLocalDateTimeUtc().date
                        map[sDate] = (map[sDate] ?: 0) + 1
                    }

                    if (entity is FinanceEntity.Transaction) {
                        for (w in entity.withdrawal) {
                            val sDate = w.createdAt.toLocalDateTimeUtc().date
                            map[sDate] = (map[sDate] ?: 0) + 1
                        }
                    }
                }
                map
            }
        }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val currentWeekDerived: StateFlow<List<LocalDate>> = currentWeekFlow
        .map { getCurrentWeekUseCase(it) }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val currentDateDerived = combine(
        currentWeekFlow,
        uiState.map { it.date }
    ) { week, date ->
        getCurrentDateUseCase(week, date)
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), LocalDate.now())

    val screenData: StateFlow<AllScreenData> = combine(
        uiState,
        groupedWeeklyData,
        professionalSummary,
        activityCounts,
        currentWeekDerived,
        currentDateDerived
    ) { flows ->
        AllScreenData(
            uiState = flows[0] as AllUiState,
            groupedWeeklyData = flows[1] as DataState<List<Pair<LocalDate, List<DataSettlement>>>>,
            professionalSummary = flows[2] as DataState<ProfessionalSummary>,
            activityCounts = flows[3] as DataState<Map<LocalDate, Int>>,
            currentWeekDerived = flows[4] as List<LocalDate>,
            currentDateDerived = flows[5] as LocalDate
        )
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), AllScreenData())

    fun updateSelectedTabIndex(index: Int) =
        _uiState.update { it.copy(selectedTabIndex = index) }

    fun updateCurrentWeek(dates: List<LocalDate>) =
        _uiState.update { it.copy(currentWeek = dates) }

    fun updateWeekDays(dates: List<LocalDate>) =
        _uiState.update { it.copy(dates = dates) }
}
