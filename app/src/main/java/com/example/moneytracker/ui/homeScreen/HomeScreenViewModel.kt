package com.example.moneytracker.ui.homeScreen

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetUiState
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.helper.isForYesterday
import com.example.moneytracker.ui.components.charts.collections.DonutChartData
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.topPanel.TopBarNav
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    val accountService: AccountServices,
    private val dataStorage: DataStorage
) : ViewModel() {

    val userState = accountService.userState

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var isDescriptionIconVisible by mutableStateOf(false)
        private set
    var isBottomSheetContentLoading by mutableStateOf(true)
        private set
    var datasetUiState by mutableStateOf<DatasetUiState>(DatasetUiState.Loading)
        private set

    init {
        observeUserAndDatasets()
    }

    /*******************
     * Public actions
     *******************/

    @RequiresApi(Build.VERSION_CODES.O)
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

//        state.datasets.filter { it.isForToday }
//            .filter { it.dataType != DataType.GOAL }
//            .groupBy { it.dataType }
//            .values.toList()
//            .map { lst ->
//                val firstItemInList = lst[0]
//                val amount = lst.sumOf { it.amount }.toFloat()
//                val colorInt = ContextCompat.getColor(context, firstItemInList.dataType.color)
//                val color = Color(colorInt)
//                val title = firstItemInList.dataType.text
//
//                DonutChartData(
//                    amount,
//                    color = color,
//                    title = title
//                )
//            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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


    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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


    fun addData(dataset: Dataset) {
        viewModelScope.launch {
            dataStorage.addData(userState.value!!.uid, dataset = dataset)
        }
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

    fun updateTopTitle(topBarNav: TopBarNav) {
        _uiState.value = _uiState.value.copy(topTitle = topBarNav)
    }

    fun updateIsUserDropdownVisible(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isUserDropdownVisible = isVisible)
    }

    fun updateIsLogOutLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLogOutLoading = isLoading)
    }

    fun updateOnModelBottomSheetShow(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(isBottomSheetOpen = isVisible)
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
