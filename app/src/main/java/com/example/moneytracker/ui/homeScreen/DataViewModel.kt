package com.example.moneytracker.ui.homeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.usecase.DatasetOperationsUseCase
import com.example.moneytracker.ui.usecase.GetCurrentDateUseCase
import com.example.moneytracker.ui.usecase.GetCurrentWeekUseCase
import com.example.moneytracker.ui.usecase.GetLenOfActivatesUseCase
import com.example.moneytracker.ui.usecase.GetTodayDatasetsUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataAdjustUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDatasetsUseCase
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import com.example.moneytracker.ui.usecase.ScheduleAlarmUseCase
import com.example.moneytracker.ui.usecase.SortTodayDataAdjustUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val accountService: AccountServices,
    private val datasetOperationsUseCase: DatasetOperationsUseCase,
    private val observeUserDataUseCase: ObserveUserDataUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val sortTodayDataAdjustUseCase: SortTodayDataAdjustUseCase,
    private val getYesterdayDataAdjustUseCase: GetYesterdayDataAdjustUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentWeekUseCase: GetCurrentWeekUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getTodayDatasetsUseCase: GetTodayDatasetsUseCase,
    private val getYesterdayDatasetsUseCase: GetYesterdayDatasetsUseCase,
    private val getLenOfActivatesUseCase: GetLenOfActivatesUseCase,
    private val routineWorker: RoutineWorkerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var isBottomSheetContentLoading by mutableStateOf(true)
        private set
    var datasetState by mutableStateOf<DatasetState>(DatasetState.Loading)
        private set

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            observeUserDataUseCase(
                accountService.userState.map { it?.uid }
            ).collect { homeData ->
                datasetState = homeData.datasetState
                _uiState.update {
                    it.copy(
                        datasets = homeData.datasets,
                        info = homeData.info,
                        error = homeData.error
                    )
                }
            }
        }
    }

    /*******************
     * Data Flows
     *******************/

    val getCurrentWeek: StateFlow<List<LocalDate>> = uiState
        .map { state -> getCurrentWeekUseCase(state.currentWeek) }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val getAllCurrentDate: StateFlow<LocalDate> = uiState
        .map { state ->
            getCurrentDateUseCase(
                currentWeek = state.currentWeek,
                fallbackDate = state.date
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LocalDate.now())

    val fetchLiveChangeDataset: Flow<List<Dataset>> = uiState
        .map { it.datasets }
        .distinctUntilChanged()

    val weeklyData: StateFlow<List<DataAdjust>> = uiState
        .map { state -> getWeeklyDataUseCase(state.datasets, state.dates) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayDatasets: StateFlow<List<Dataset>> = uiState
        .map { state -> getTodayDatasetsUseCase(state.datasets) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val yesterdayDatasets: StateFlow<List<Dataset>> = uiState
        .map { state -> getYesterdayDatasetsUseCase(state.datasets) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /*******************
     * Sorting
     *******************/

    fun sortTodayDataAdjust(
        timeSorting: SortType,
        categorySorting: String?,
        paymentSorting: PaymentMethod?,
        alphabeticalOrder: SortType,
        amountSorting: SortType,
        take: Int? = null
    ): StateFlow<List<DataAdjust>> = uiState.map { state ->
        sortTodayDataAdjustUseCase(
            state.datasets,
            timeSorting,
            categorySorting,
            paymentSorting,
            alphabeticalOrder,
            amountSorting,
            take
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun sortYesterdayDataAdjust(): StateFlow<List<DataAdjust>> = uiState.map { state ->
        getYesterdayDataAdjustUseCase(state.datasets)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /*******************
     * Operations
     *******************/

    fun addData(dataset: Dataset) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.addData(uid, dataset)
        }
    }

    fun updateData(oldDataset: Dataset, newDataset: Dataset) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.updateData(uid, oldDataset, newDataset)
        }
    }

    fun removeData(dataset: Dataset) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.removeData(uid, dataset)
        }
    }

    fun addAdjustmentData(dataset: Dataset, adjustment: Adjustment) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.addAdjustment(uid, dataset.id, adjustment)
        }
    }

    fun updateAdjustmentData(
        dataset: Dataset,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.updateAdjustment(uid, dataset.id, oldAdjustment, newAdjustment)
        }
    }

    fun removeAdjustmentDataset(datasetId: String, adjustment: Adjustment) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            datasetOperationsUseCase.removeAdjustment(uid, datasetId, adjustment)
        }
    }

//    fun setAlarm(dataset: Dataset) {
//        val uid = accountService.userState.value?.uid ?: return
//        val isRoutine = dataset.routine.routine != Routine.Nothing
//
//        if (isRoutine || dataset.dataType == DataType.GOAL) {
//            val triggerMillis = if (isRoutine) {
//                dataset.routine.triggerMillis
//            } else {
//                dataset.deadlineDateTime.toDate().time
//            }
//
//            routineWorker(
//                userId = uid,
//                datasetId = dataset.id,
//                triggerMillis = triggerMillis,
//                isRoutine = isRoutine
//            )
//        }
//    }

    fun beginTheWork(dataset: Dataset) {
        val uid = accountService.userState.value?.uid ?: return
        if (dataset.routine.routine == Routine.Nothing) return

        routineWorker(
            userId = uid,
            datasetId = dataset.id,
            triggerMillis = dataset.routine.triggerMillis,
            isRoutine = true
        )
    }

    /*******************
     * UI State Updates
     *******************/

    fun updateCurrentWeek(dates: List<java.time.LocalDate>) {
        _uiState.update { it.copy(currentWeek = dates) }
    }

    fun updateWeekDays(dates: List<LocalDate>) {
        _uiState.update { it.copy(dates = dates) }
    }

    fun updateSelectedTabIndex(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun updateTopTitle(topBarNav: TopBarNav) {
        _uiState.update { it.copy(topTitle = topBarNav) }
    }

    fun updateOnDatasetModelBottomSheetShow(isVisible: Boolean) {
        _uiState.update { it.copy(isDatasetBottomSheetOpen = isVisible) }
    }

    fun updateOnAdjustModelBottomSheetShow(isVisible: Boolean) {
        _uiState.update { it.copy(isAdjustmentBottomSheetOpen = isVisible) }
    }

    fun updateIsBottomSheetContentLoading(isLoading: Boolean) {
        isBottomSheetContentLoading = isLoading
    }

    fun getLenOfActivates(date: LocalDate): Int {
        return getLenOfActivatesUseCase(uiState.value.datasets, date)
    }
}
