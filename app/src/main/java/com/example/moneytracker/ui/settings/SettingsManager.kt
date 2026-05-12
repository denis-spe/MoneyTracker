// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeConfig {
    SYSTEM, LIGHT, DARK
}

class SettingsManager(private val context: Context) {
    private val THEME_KEY = intPreferencesKey("theme_option")
    private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    private val CUSTOM_BACKGROUND_KEY = longPreferencesKey("custom_background")
    private val CONTENT_COLOR_KEY = longPreferencesKey("content_color")
    private val AUTO_BACKGROUND_KEY = longPreferencesKey("auto_background")
    private val AUTO_TEXT_KEY = longPreferencesKey("auto_text")
    private val THEME_COLOR_KEY = longPreferencesKey("theme_color")

    val themeConfig: Flow<ThemeConfig> = context.dataStore.data
        .map { preferences ->
            val themeIndex = preferences[THEME_KEY] ?: ThemeConfig.SYSTEM.ordinal
            ThemeConfig.entries.getOrElse(themeIndex) { ThemeConfig.SYSTEM }
        }

    val dynamicColor: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] ?: true
        }

    val customBackground: Flow<Long?> = context.dataStore.data.map { it[CUSTOM_BACKGROUND_KEY] }
    val contentColor: Flow<Long?> = context.dataStore.data.map { it[CONTENT_COLOR_KEY] }
    val autoBackground: Flow<Long?> = context.dataStore.data.map { it[AUTO_BACKGROUND_KEY] }
    val autoText: Flow<Long?> = context.dataStore.data.map { it[AUTO_TEXT_KEY] }
    val themeColor: Flow<Long?> = context.dataStore.data.map { it[THEME_COLOR_KEY] }

    suspend fun setThemeConfig(themeConfig: ThemeConfig) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeConfig.ordinal
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    suspend fun setCustomBackground(color: Long) {
        context.dataStore.edit { it[CUSTOM_BACKGROUND_KEY] = color }
    }

    suspend fun setContentColor(color: Long) {
        context.dataStore.edit { it[CONTENT_COLOR_KEY] = color }
    }

    suspend fun setAutoBackground(color: Long) {
        context.dataStore.edit { it[AUTO_BACKGROUND_KEY] = color }
    }

    suspend fun setAutoText(color: Long) {
        context.dataStore.edit { it[AUTO_TEXT_KEY] = color }
    }

    suspend fun setThemeColor(color: Long) {
        context.dataStore.edit { it[THEME_COLOR_KEY] = color }
    }
}
