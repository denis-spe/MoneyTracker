package com.example.moneytracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val themeConfig: StateFlow<ThemeConfig> = settingsManager.themeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.SYSTEM
        )

    val dynamicColor: StateFlow<Boolean> = settingsManager.dynamicColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val customBackground: StateFlow<Long?> = settingsManager.customBackground
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val contentColor: StateFlow<Long?> = settingsManager.contentColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val autoBackground: StateFlow<Long?> = settingsManager.autoBackground
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val autoText: StateFlow<Long?> = settingsManager.autoText
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val themeColor: StateFlow<Long?> = settingsManager.themeColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun setThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            settingsManager.setThemeConfig(themeConfig)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDynamicColor(enabled)
        }
    }

    fun setCustomBackground(color: Long) {
        viewModelScope.launch { settingsManager.setCustomBackground(color) }
    }

    fun setContentColor(color: Long) {
        viewModelScope.launch { settingsManager.setContentColor(color) }
    }

    fun setAutoBackground(color: Long) {
        viewModelScope.launch { settingsManager.setAutoBackground(color) }
    }

    fun setAutoText(color: Long) {
        viewModelScope.launch { settingsManager.setAutoText(color) }
    }

    fun setThemeColor(color: Long) {
        viewModelScope.launch {
            settingsManager.setThemeColor(color)
        }
    }
}
