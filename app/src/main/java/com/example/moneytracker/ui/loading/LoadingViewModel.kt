package com.example.moneytracker.ui.loading

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel


class LoadingViewModel : ViewModel() {
    var content: (@Composable () -> Unit)? = null

    fun setScreenContent(
        content: @Composable () -> Unit
    ) {
        this.content = content
    }
}