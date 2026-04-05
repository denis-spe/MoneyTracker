package com.example.moneytracker.ui.screenManager

import androidx.lifecycle.ViewModel
import com.example.moneytracker.backend.auth.AccountServices
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScreenManagerViewModel @Inject constructor(
    val account: AccountServices
) : ViewModel()
