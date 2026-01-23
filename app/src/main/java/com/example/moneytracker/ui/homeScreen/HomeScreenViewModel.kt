package com.example.moneytracker.ui.homeScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.R
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetUiState
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.helper.isForYesterday
import com.example.moneytracker.ui.homeScreen.topPanel.CurrentTopTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
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
    private val _isIconDialogVisible = MutableStateFlow(false)

    private val _selectedIcon = MutableStateFlow(Pair("description", R.drawable.description))

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

    fun addRepayData(dataset: Dataset, adjustment: Adjustment) {
        viewModelScope.launch {
            try {
                dataStorage.addAdjustmentDataset(
                    userState.value!!.uid,
                    datasetId = dataset.id,
                    adjustment = adjustment
                )
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "addRepayData failed", e)
            }
        }
    }

    fun updateTopTitle(currentTopTitle: CurrentTopTitle) {
        _uiState.value = _uiState.value.copy(topTitle = currentTopTitle)
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
