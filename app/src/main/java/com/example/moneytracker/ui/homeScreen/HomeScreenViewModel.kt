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
import com.example.moneytracker.ui.homeScreen.topTitle.CurrentTopTitle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        _uiState.value = _uiState.value.copy(onModelBottomSheetShow = isVisible)
    }

    fun updateIsDescriptionIconVisible(isVisible: Boolean) {
        isDescriptionIconVisible = isVisible
    }

    fun updateDescriptionIcon(icon: Int) {
        _uiState.value = _uiState.value.copy(descriptionIcon = icon)
    }

    private fun fetchDataset() {
        viewModelScope.launch {
            // ensure we have a user id to subscribe with
            val uid = userState.value?.uid ?: return@launch

            // Launch two concurrent collectors so neither flow blocks the other
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

            launch {
                dataStorage.getWholeDatasets(uid)
                    .catch { e ->
                        _uiState.value = uiState.value.copy(error = e.message ?: "Unknown error")
                    }
                    .collect { data ->
                        _uiState.value = _uiState.value.copy(datasets = data, isLoading = true)
                        delay(250)
                        _uiState.value = _uiState.value.copy(isLoading = false)
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