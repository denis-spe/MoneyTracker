// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

    val themeConfig: Flow<ThemeConfig> = context.dataStore.data
        .map { preferences ->
            val themeIndex = preferences[THEME_KEY] ?: ThemeConfig.SYSTEM.ordinal
            ThemeConfig.entries.getOrElse(themeIndex) { ThemeConfig.SYSTEM }
        }

    val dynamicColor: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] ?: true
        }

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
}
