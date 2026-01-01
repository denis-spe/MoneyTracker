package com.example.moneytracker.ui.homeScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetUiState
import com.example.moneytracker.backend.storage.Repay
import com.example.moneytracker.ui.homeScreen.topPanel.CurrentTopTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    var datasetUiState by mutableStateOf<DatasetUiState>(DatasetUiState.Loading)
        private set

    /**
     * Create a new user with the current user id
     */
    fun createUserWithId() {
        viewModelScope.launch {
            dataStorage.createUserWithId(id = userState.value!!.uid)
        }
    }

    init {
        fetchDataset()
    }

    fun addData(dataset: Dataset) {
        viewModelScope.launch {
            dataStorage.addData(userState.value!!.uid, dataset = dataset)
        }
    }

    fun addRepayData(dataset: Dataset, repay: Repay) {
        // Log early so we can see UI triggered this method
        Log.d(
            "HomeScreenViewModel",
            "addRepayData called for dataset=${dataset.label} id=${dataset.id}"
        )

        // If id is null, attempt to find a matching dataset that may have been migrated/updated
        var targetId: String? = dataset.id
        if (targetId == null) {
            val match =
                _uiState.value.datasets.find { it.label == dataset.label && it.dateTime == dataset.dateTime }
            if (match != null && match.id != null) {
                targetId = match.id
                Log.d(
                    "HomeScreenViewModel",
                    "Found matching migrated dataset id=$targetId for label=${dataset.label}"
                )
            } else {
                Log.w(
                    "HomeScreenViewModel",
                    "Dataset.id is null and no matching migrated dataset found. Dataset: $dataset"
                )
                return
            }
        }

        viewModelScope.launch {
            try {
                dataStorage.addRepayToDataset(
                    userState.value!!.uid,
                    datasetId = targetId,
                    repay = repay
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


    private fun fetchDataset() {
        viewModelScope.launch {
            // ensure we have a user id to subscribe with
            val uid = userState.value?.uid ?: return@launch

            // Migration: ensure stored datasets have ids so repay can match by id
            try {
                dataStorage.ensureDatasetIds(uid)
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "ensureDatasetIds failed", e)
            }

            // Launch two concurrent collectors so neither flow blocks the other
            launch {
                dataStorage.getWholeDatasets(
                    uid,
                    onSuccess = {
                        datasetUiState = DatasetUiState.Success
                    },
                    onFailure = {
                        datasetUiState = DatasetUiState.Error(it?.message)
                    }
                )
                    .catch { e ->
                        _uiState.value = uiState.value.copy(error = e.message ?: "Unknown error")
                    }
                    .collect { data ->
                        // Update datasets directly without artificial delays to avoid UI jank
                        _uiState.value = _uiState.value.copy(datasets = data)
                    }
            }

            launch {
                dataStorage.getInfo(uid)
                    .catch { e ->
                        _uiState.value = uiState.value.copy(error = e.message ?: "Unknown error")
                    }
                    .collect { info ->
                        Log.d("HomeScreenColor", info.toString())
                        _uiState.value = _uiState.value.copy(info = info)
                    }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}