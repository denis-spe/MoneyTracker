package com.example.moneytracker.ui.homeScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    accountService: AccountServices,
    private val dataStorage: DataStorage
) : ViewModel() {
    val userState = accountService.userState

    fun createUserWithId(id: String) {
        viewModelScope.launch {
            dataStorage.addData(
                id,
                Dataset(
                    userId = id,
                    amount = 0.0,
                    dataType = DataType.INCOME,
                    label = "First Income"
                )
            )
//            dataStorage.createUserWithId(id = id)
        }
    }
}