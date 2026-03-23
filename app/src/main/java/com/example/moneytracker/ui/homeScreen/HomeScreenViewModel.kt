package com.example.moneytracker.ui.homeScreen

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.alarmManager.AlarmItem
import com.example.moneytracker.backend.alarmManager.AndroidAlarm
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetUiState
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.helper.isForYesterday
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val accountService: AccountServices,
    private val dataStorage: DataStorage,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val userState = accountService.userState

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()


    var isDescriptionIconVisible by mutableStateOf(false)
        private set
    var isBottomSheetContentLoading by mutableStateOf(true)
        private set
    var datasetUiState by mutableStateOf<DatasetUiState>(DatasetUiState.Loading)
        private set

    // Backing property for private emission
    private val _navigationEvents = MutableSharedFlow<Unit>()

    // Public read-only SharedFlow for the UI
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        observeUserAndDatasets()
    }

    /*******************
     * Public actions
     *******************/

    fun launchSnackBarHostState(message: String) {
        viewModelScope.launch {
            _snackBarHostState.value.showSnackbar(message)
        }
    }

    fun showActionNotification(message: String, color: Color) {
        _uiState.value = _uiState.value.copy(
            isActionNotificationVisible = true,
            actionNotificationMessage = message,
            actionNotificationColor = color
        )
    }

    fun dismissActionNotification() {
        _uiState.value = _uiState.value.copy(isActionNotificationVisible = false)
    }

    fun handleLogout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true) // Start loading
            try {
                signOut() // Your actual logout logic (suspend function)
                _navigationEvents.emit(Unit) // Trigger navigation
            } finally {
                _uiState.value =
                    _uiState.value.copy(isLoading = false) // Stop loading even if it fails
            }
        }
    }


    val getCurrentWeek: Flow<List<LocalDate>> = uiState.map { state ->
        state.currentWeek.map { it.toKotlinLocalDate() }
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    val getAllCurrentDate: Flow<LocalDate> = uiState.map { state ->
        val currentWeek = state.currentWeek.map { it.toKotlinLocalDate() }
        if (_uiState.value.currentWeek.isEmpty()) state.date
        else currentWeek.first()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LocalDate.now()
    )

    val fetchLiveChangeDataset: Flow<List<Dataset>> = uiState.map { state ->
        state.datasets
    }.distinctUntilChanged().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun todayChartData(context: Context): Flow<List<DonutChartData>> = uiState.map { state ->
        val adjust = state.datasets.map { dataset ->
            dataset.adjustment.map { adjustment ->
                adjustment.dataset = dataset
                DataAdjust.Adjust(adjustment)
            }
        }
        val data = state.datasets.map { dataset ->
            DataAdjust.Data(dataset)
        }
        val coupledData = (adjust.flatten() + data).filter {
            when (it) {
                is DataAdjust.Data -> it.dataset.isForToday
                is DataAdjust.Adjust -> it.adjustment.isForToday
            }
        }

        val dataAdjust = coupledData.groupBy {
            when (it) {
                is DataAdjust.Data -> it.dataset.dataType
                is DataAdjust.Adjust -> it.adjustment.adjustmentType
            }
        }
            .values.toList()

        dataAdjust.map { lst ->
            val firstItemInList = lst[0]
            val colorResId = when (firstItemInList) {
                is DataAdjust.Data -> firstItemInList.dataset.dataType.color
                is DataAdjust.Adjust -> firstItemInList.adjustment.adjustmentType.color
            }
            val title = when (firstItemInList) {
                is DataAdjust.Data -> firstItemInList.dataset.dataType.text
                is DataAdjust.Adjust -> firstItemInList.adjustment.adjustmentType.text
            }

            val amount = lst.sumOf {
                when (it) {
                    is DataAdjust.Data -> it.dataset.amount
                    is DataAdjust.Adjust -> it.adjustment.amount
                }
            }.toFloat()

            val colorInt = ContextCompat.getColor(context, colorResId)
            val color = Color(colorInt)

            DonutChartData(
                amount,
                color = color,
                title = title
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun getLenOfActivates(date: LocalDate): Int {
        val datasetLen = uiState.value.datasets.filter {
            it.dateTime.toLocalDateTimeUtc().date == date
        }.size

        val adjustLen = uiState.value.datasets.flatMap {
            it.adjustment
        }.filter {
            it.dateTime.toLocalDateTimeUtc().date == date
        }.size

        return datasetLen + adjustLen
    }

    val weeklyData: StateFlow<List<DataAdjust>> = uiState
        .map { state ->
            val adjust = state.datasets.map { dataset ->
                dataset.adjustment.map { adjustment ->
                    adjustment.dataset = dataset
                    DataAdjust.Adjust(adjustment)
                }
            }
            val data = state.datasets.map { dataset ->
                DataAdjust.Data(dataset)
            }
            val coupledData = (adjust.flatten() + data).filter {
                when (it) {
                    is DataAdjust.Data ->
                        it.dataset.dateTime.toLocalDateTimeUtc().date in state.dates

                    is DataAdjust.Adjust ->
                        it.adjustment.dateTime.toLocalDateTimeUtc().date in state.dates
                }
            }
            coupledData
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun sortTodayDataAdjust(
        timeSorting: SortType,
        categorySorting: String?,
        paymentSorting: PaymentMethod?,
        alphabeticalOrder: SortType,
        amountSorting: SortType,
        take: Int? = null
    ): StateFlow<List<DataAdjust>> =
        uiState.map { state ->
            val adjust = state.datasets.map { dataset ->
                dataset.adjustment.map { adjustment ->
                    adjustment.dataset = dataset
                    DataAdjust.Adjust(adjustment)
                }
            }
            val data = state.datasets.map { dataset ->
                DataAdjust.Data(dataset)
            }
            var coupledData = (adjust.flatten() + data).filter {
                when (it) {
                    is DataAdjust.Data -> it.dataset.isForToday
                    is DataAdjust.Adjust -> it.adjustment.isForToday
                }
            }

            // Sorting with time
            coupledData = when (timeSorting) {
                SortType.Ascending -> coupledData.sortedBy {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.dateTime
                        is DataAdjust.Adjust -> it.adjustment.dateTime
                    }
                }

                SortType.Descending -> coupledData.sortedByDescending {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.dateTime
                        is DataAdjust.Adjust -> it.adjustment.dateTime
                    }
                }

                SortType.Initial -> coupledData
            }

            coupledData =
                if (categorySorting == null || categorySorting == "Initial") coupledData else
                    coupledData.filter {
                        when (it) {
                            is DataAdjust.Data ->
                                it.dataset.dataType.text == categorySorting

                            is DataAdjust.Adjust ->
                                it.adjustment.adjustmentType.text == categorySorting
                        }
                    }

            coupledData = if (paymentSorting == null) coupledData else
                coupledData.filter {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.paymentMethod == paymentSorting
                        is DataAdjust.Adjust -> it.adjustment.paymentMethod == paymentSorting
                    }
                }

            coupledData = when (alphabeticalOrder) {
                SortType.Ascending -> coupledData.sortedBy {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.label
                        is DataAdjust.Adjust -> it.adjustment.label
                    }
                }

                SortType.Descending -> coupledData.sortedByDescending {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.label
                        is DataAdjust.Adjust -> it.adjustment.label
                    }
                }

                SortType.Initial -> coupledData
            }

            coupledData = when (amountSorting) {
                SortType.Ascending -> coupledData.sortedBy {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.amount
                        is DataAdjust.Adjust -> it.adjustment.amount
                    }
                }

                SortType.Descending -> coupledData.sortedByDescending {
                    when (it) {
                        is DataAdjust.Data -> it.dataset.amount
                        is DataAdjust.Adjust -> it.adjustment.amount
                    }
                }

                SortType.Initial -> coupledData
            }


            if (take != null)
                coupledData = coupledData.take(take)


            coupledData

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun sortYesterdayDataAdjust(): StateFlow<List<DataAdjust>> =
        uiState.map { state ->
            val adjust = state.datasets.map { dataset ->
                dataset.adjustment.map { adjustment ->
                    adjustment.dataset = dataset
                    DataAdjust.Adjust(adjustment)
                }
            }
            val data = state.datasets.map { dataset ->
                DataAdjust.Data(dataset)
            }
            val coupledData = (adjust.flatten() + data).filter {
                when (it) {
                    is DataAdjust.Data -> it.dataset.isForYesterday
                    is DataAdjust.Adjust -> it.adjustment.isForYesterday
                }
            }

            coupledData
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    val todayDatasets: StateFlow<List<Dataset>> =
        uiState
            .map { state ->
                state.datasets.filter { it.isForToday }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val yesterdayDatasets: StateFlow<List<Dataset>> =
        uiState
            .map { state ->
                state.datasets.filter { it.isForYesterday }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    fun addData(dataset: Dataset): String {
        var datasetId = dataset.id
        viewModelScope.launch {
            datasetId = dataStorage.addData(userState.value!!.uid, dataset = dataset)
        }
        return datasetId
    }

    fun updateData(oldDataset: Dataset, newDataset: Dataset) {
        viewModelScope.launch {
            dataStorage.updateDataset(
                userState.value!!.uid,
                oldDataset = oldDataset,
                newDataset = newDataset
            )
        }
    }

    fun removeData(dataset: Dataset) {
        viewModelScope.launch {
            dataStorage.removeDataset(userState.value!!.uid, dataset = dataset)
        }
    }

    fun removeAdjustmentDataset(datasetId: String, adjustment: Adjustment) {
        viewModelScope.launch {
            dataStorage.removeAdjustmentDataset(
                userState.value!!.uid,
                datasetId = datasetId,
                adjustment = adjustment
            )
        }
    }

    fun addAdjustmentData(dataset: Dataset, adjustment: Adjustment) {
        viewModelScope.launch {
            try {
                dataStorage.addAdjustmentDataset(
                    userState.value!!.uid,
                    datasetId = dataset.id,
                    adjustment = adjustment
                )
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "addAdjustmentData failed", e)
            }
        }
    }

    fun setAlarm(datasetId: String, routineData: RoutineData) {
        val androidAlarm = AndroidAlarm(context = context)

        val alarmItem = AlarmItem(
            datasetId = datasetId,
            userId = userState.value!!.uid,
            routineData = routineData,
        )

        androidAlarm.schedule(alarmItem)

    }

    fun updateAdjustmentData(
        dataset: Dataset,
        oldAdjustment: Adjustment,
        newAdjustment: Adjustment
    ) {
        viewModelScope.launch {
            try {
                dataStorage.updateAdjustmentDataset(
                    userState.value!!.uid,
                    datasetId = dataset.id,
                    oldAdjustment = oldAdjustment,
                    newAdjustment = newAdjustment
                )
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "addAdjustmentData failed", e)
            }
        }
    }

    fun updateCurrentWeek(dates: List<java.time.LocalDate>) {
        _uiState.value = _uiState.value.copy(currentWeek = dates)
    }

    fun updateWeekDays(dates: List<LocalDate>) {
        _uiState.value = _uiState.value.copy(dates = dates)
    }

    fun updateSelectedTabIndex(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTabIndex = index)
    }

    fun updateTopTitle(topBarNav: TopBarNav) {
        _uiState.value = _uiState.value.copy(topTitle = topBarNav)
    }

    fun updateIsUserDropdownVisible(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isUserDropdownVisible = isVisible)
    }

    fun updateIsLogOutLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLogOutLoading = isLoading)
    }

    fun updateOnDatasetModelBottomSheetShow(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isDatasetBottomSheetOpen = isVisible)
    }

    fun updateOnAdjustModelBottomSheetShow(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isAdjustmentBottomSheetOpen = isVisible)
    }

    fun updateIsDescriptionIconVisible(isVisible: Boolean) {
        isDescriptionIconVisible = isVisible
    }

    fun updateIsBottomSheetContentLoading(isLoading: Boolean) {
        isBottomSheetContentLoading = isLoading
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    /*******************
     * Private: observe user and start collectors when uid exists
     *******************/
    private fun observeUserAndDatasets() {
        viewModelScope.launch {
            // collectLatest ensures the inner block is canceled when user changes.
            userState.collectLatest { user ->
                if (user == null) {
                    // user signed out or not ready yet -> clear UI state
                    datasetUiState = DatasetUiState.Loading
                    _uiState.value = _uiState.value.copy(datasets = emptyList())
                    _uiState.value =
                        _uiState.value.copy(info = _uiState.value.info) // keep current info if you want
                    return@collectLatest
                }

                val uid = user.uid

                // Migration attempt; failures are logged but should not crash the collector
                try {
                    dataStorage.ensureDatasetIds(uid)
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "ensureDatasetIds failed", e)
                }

                // When user becomes available, run the two collectors concurrently.
                // If user changes, collectLatest cancels this coroutine and restarts.
                try {
                    coroutineScope {
                        // datasets collector
                        launch {
                            dataStorage.getWholeDatasets(
                                uid,
                                onSuccess = { datasetUiState = DatasetUiState.Success },
                                onFailure = { datasetUiState = DatasetUiState.Error(it?.message) }
                            )
                                .catch { e ->
                                    Log.e("HomeScreenViewModel", "getWholeDatasets flow error", e)
                                    _uiState.value =
                                        _uiState.value.copy(error = e.message ?: "Unknown error")
                                }
                                .collect { list ->
                                    Log.d("HomeScreenViewModel", "datasets received=${list.size}")
                                    _uiState.value = _uiState.value.copy(datasets = list)
                                }
                        }

                        // info collector
                        launch {
                            dataStorage.getInfo(uid)
                                .catch { e ->
                                    Log.e("HomeScreenViewModel", "getInfo flow error", e)
                                    _uiState.value =
                                        _uiState.value.copy(error = e.message ?: "Unknown error")
                                }
                                .collect { info ->
                                    Log.d("HomeScreenViewModel", "info received=$info")
                                    _uiState.value = _uiState.value.copy(info = info)
                                }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "Error while collecting after user available", e)
                    // keep datasetUiState updated on failure
                    datasetUiState = DatasetUiState.Error(e.message)
                }
            }
        }
    }
}
