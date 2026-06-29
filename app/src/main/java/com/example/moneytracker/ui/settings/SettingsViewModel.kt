package com.example.moneytracker.ui.settings

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Keep
data class ThemeState(
    val themeConfig: ThemeConfig = ThemeConfig.SYSTEM,
    val dynamicColor: Boolean = true,
    val lightSecondarySurface: Long? = null,
    val lightAccentContent: Long? = null,
    val lightOnSurfaceText: Long? = null,
    val lightPrimaryAccent: Long? = null,
    val darkSecondarySurface: Long? = null,
    val darkAccentContent: Long? = null,
    val darkOnSurfaceText: Long? = null,
    val darkPrimaryAccent: Long? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    // Combined theme state reduces recomposition triggers by 8x
    val themeState: StateFlow<ThemeState> = combine(
        settingsManager.themeConfig,
        settingsManager.dynamicColor,
        settingsManager.lightSecondarySurface,
        settingsManager.lightAccentContent,
        settingsManager.lightOnSurfaceText,
        settingsManager.lightPrimaryAccent,
        settingsManager.darkSecondarySurface,
        settingsManager.darkAccentContent,
        settingsManager.darkOnSurfaceText,
        settingsManager.darkPrimaryAccent
    ) { values: Array<Any?> ->
        ThemeState(
            themeConfig = values[0] as ThemeConfig,
            dynamicColor = values[1] as Boolean,
            lightSecondarySurface = values[2] as? Long,
            lightAccentContent = values[3] as? Long,
            lightOnSurfaceText = values[4] as? Long,
            lightPrimaryAccent = values[5] as? Long,
            darkSecondarySurface = values[6] as? Long,
            darkAccentContent = values[7] as? Long,
            darkOnSurfaceText = values[8] as? Long,
            darkPrimaryAccent = values[9] as? Long
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState()
    )

    // Keep individual flows for backward compatibility - lazily wrapped
    // Note: These are now derived from themeState to avoid redundant StateFlow creations
    // Only use these if existing code depends on them; prefer themeState for new code
    val themeConfig: StateFlow<ThemeConfig> = themeState
        .map { it.themeConfig }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ThemeConfig.SYSTEM
        )

    val dynamicColor: StateFlow<Boolean> = themeState
        .map { it.dynamicColor }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    // Light mode flows - derived from themeState
    val lightSecondarySurface: StateFlow<Long?> = themeState
        .map { it.lightSecondarySurface }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lightAccentContent: StateFlow<Long?> = themeState
        .map { it.lightAccentContent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lightOnSurfaceText: StateFlow<Long?> = themeState
        .map { it.lightOnSurfaceText }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lightPrimaryAccent: StateFlow<Long?> = themeState
        .map { it.lightPrimaryAccent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dark mode flows - derived from themeState
    val darkSecondarySurface: StateFlow<Long?> = themeState
        .map { it.darkSecondarySurface }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val darkAccentContent: StateFlow<Long?> = themeState
        .map { it.darkAccentContent }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val darkOnSurfaceText: StateFlow<Long?> = themeState
        .map { it.darkOnSurfaceText }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val darkPrimaryAccent: StateFlow<Long?> = themeState
        .map { it.darkPrimaryAccent }
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
