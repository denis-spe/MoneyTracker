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

    // Light mode keys
    private val LIGHT_SECONDARY_SURFACE_KEY = longPreferencesKey("light_secondary_surface")
    private val LIGHT_ACCENT_CONTENT_KEY = longPreferencesKey("light_accent_content")
    private val LIGHT_ON_SURFACE_TEXT_KEY = longPreferencesKey("light_on_surface_text")
    private val LIGHT_PRIMARY_ACCENT_KEY = longPreferencesKey("light_primary_accent")

    // Dark mode keys
    private val DARK_SECONDARY_SURFACE_KEY = longPreferencesKey("dark_secondary_surface")
    private val DARK_ACCENT_CONTENT_KEY = longPreferencesKey("dark_accent_content")
    private val DARK_ON_SURFACE_TEXT_KEY = longPreferencesKey("dark_on_surface_text")
    private val DARK_PRIMARY_ACCENT_KEY = longPreferencesKey("dark_primary_accent")

    val themeConfig: Flow<ThemeConfig> = context.dataStore.data
        .map { preferences ->
            val themeIndex = preferences[THEME_KEY] ?: ThemeConfig.SYSTEM.ordinal
            ThemeConfig.entries.getOrElse(themeIndex) { ThemeConfig.SYSTEM }
        }

    val dynamicColor: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DYNAMIC_COLOR_KEY] ?: true
        }

    // Light mode flows
    val lightSecondarySurface: Flow<Long?> =
        context.dataStore.data.map { it[LIGHT_SECONDARY_SURFACE_KEY] }
    val lightAccentContent: Flow<Long?> =
        context.dataStore.data.map { it[LIGHT_ACCENT_CONTENT_KEY] }
    val lightOnSurfaceText: Flow<Long?> =
        context.dataStore.data.map { it[LIGHT_ON_SURFACE_TEXT_KEY] }
    val lightPrimaryAccent: Flow<Long?> =
        context.dataStore.data.map { it[LIGHT_PRIMARY_ACCENT_KEY] }

    // Dark mode flows
    val darkSecondarySurface: Flow<Long?> =
        context.dataStore.data.map { it[DARK_SECONDARY_SURFACE_KEY] }
    val darkAccentContent: Flow<Long?> = context.dataStore.data.map { it[DARK_ACCENT_CONTENT_KEY] }
    val darkOnSurfaceText: Flow<Long?> = context.dataStore.data.map { it[DARK_ON_SURFACE_TEXT_KEY] }
    val darkPrimaryAccent: Flow<Long?> = context.dataStore.data.map { it[DARK_PRIMARY_ACCENT_KEY] }

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

    // Light mode setters
    suspend fun setLightSecondarySurface(color: Long) {
        context.dataStore.edit { it[LIGHT_SECONDARY_SURFACE_KEY] = color }
    }

    suspend fun setLightAccentContent(color: Long) {
        context.dataStore.edit { it[LIGHT_ACCENT_CONTENT_KEY] = color }
    }

    suspend fun setLightOnSurfaceText(color: Long) {
        context.dataStore.edit { it[LIGHT_ON_SURFACE_TEXT_KEY] = color }
    }

    suspend fun setLightPrimaryAccent(color: Long) {
        context.dataStore.edit { it[LIGHT_PRIMARY_ACCENT_KEY] = color }
    }

    // Dark mode setters
    suspend fun setDarkSecondarySurface(color: Long) {
        context.dataStore.edit { it[DARK_SECONDARY_SURFACE_KEY] = color }
    }

    suspend fun setDarkAccentContent(color: Long) {
        context.dataStore.edit { it[DARK_ACCENT_CONTENT_KEY] = color }
    }

    suspend fun setDarkOnSurfaceText(color: Long) {
        context.dataStore.edit { it[DARK_ON_SURFACE_TEXT_KEY] = color }
    }

    suspend fun setDarkPrimaryAccent(color: Long) {
        context.dataStore.edit { it[DARK_PRIMARY_ACCENT_KEY] = color }
    }

    suspend fun resetLightColors() {
        context.dataStore.edit { preferences ->
            preferences.remove(LIGHT_SECONDARY_SURFACE_KEY)
            preferences.remove(LIGHT_ACCENT_CONTENT_KEY)
            preferences.remove(LIGHT_ON_SURFACE_TEXT_KEY)
            preferences.remove(LIGHT_PRIMARY_ACCENT_KEY)
        }
    }

    suspend fun resetDarkColors() {
        context.dataStore.edit { preferences ->
            preferences.remove(DARK_SECONDARY_SURFACE_KEY)
            preferences.remove(DARK_ACCENT_CONTENT_KEY)
            preferences.remove(DARK_ON_SURFACE_TEXT_KEY)
            preferences.remove(DARK_PRIMARY_ACCENT_KEY)
        }
    }
}
