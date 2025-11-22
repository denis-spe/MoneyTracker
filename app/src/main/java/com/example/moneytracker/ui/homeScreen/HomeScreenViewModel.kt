package com.example.moneytracker.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.Dataset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    accountService: AccountServices,
    private val dataStorage: DataStorage
) : ViewModel() {
    val userState = accountService.userState
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

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

    private fun fetchDataset() {
        viewModelScope.launch {
            dataStorage.getWholeDatasets(userState.value!!.uid)
                .catch { e ->
                    _uiState.value = uiState.value.copy(error = e.message ?: "Unknown error")
                }
                .collect { data ->
                    _uiState.value = _uiState.value.copy(datasets = data)
                }
        }
    }

}