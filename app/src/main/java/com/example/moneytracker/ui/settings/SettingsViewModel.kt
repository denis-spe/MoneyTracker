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

    // Light mode flows
    val lightSecondarySurface = settingsManager.lightSecondarySurface
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val lightAccentContent = settingsManager.lightAccentContent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val lightOnSurfaceText = settingsManager.lightOnSurfaceText
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val lightPrimaryAccent = settingsManager.lightPrimaryAccent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dark mode flows
    val darkSecondarySurface = settingsManager.darkSecondarySurface
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val darkAccentContent = settingsManager.darkAccentContent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val darkOnSurfaceText = settingsManager.darkOnSurfaceText
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val darkPrimaryAccent = settingsManager.darkPrimaryAccent
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    // Light mode setters
    fun setLightSecondarySurface(color: Long) =
        viewModelScope.launch { settingsManager.setLightSecondarySurface(color) }

    fun setLightAccentContent(color: Long) =
        viewModelScope.launch { settingsManager.setLightAccentContent(color) }

    fun setLightOnSurfaceText(color: Long) =
        viewModelScope.launch { settingsManager.setLightOnSurfaceText(color) }

    fun setLightPrimaryAccent(color: Long) =
        viewModelScope.launch { settingsManager.setLightPrimaryAccent(color) }

    // Dark mode setters
    fun setDarkSecondarySurface(color: Long) =
        viewModelScope.launch { settingsManager.setDarkSecondarySurface(color) }

    fun setDarkAccentContent(color: Long) =
        viewModelScope.launch { settingsManager.setDarkAccentContent(color) }

    fun setDarkOnSurfaceText(color: Long) =
        viewModelScope.launch { settingsManager.setDarkOnSurfaceText(color) }

    fun setDarkPrimaryAccent(color: Long) =
        viewModelScope.launch { settingsManager.setDarkPrimaryAccent(color) }

    fun resetLightColors() = viewModelScope.launch { settingsManager.resetLightColors() }
    fun resetDarkColors() = viewModelScope.launch { settingsManager.resetDarkColors() }
}
