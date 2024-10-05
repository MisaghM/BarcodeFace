package com.misana.barcodeface.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.misana.barcodeface.domain.model.ListViewType
import com.misana.barcodeface.domain.model.SettingsData
import com.misana.barcodeface.domain.model.ThemeSelect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val ds: DataStore<Preferences>
) {
    private companion object PreferencesKey {
        val keyTheme = stringPreferencesKey("theme")
        val keyWelcomeDone = booleanPreferencesKey("welcomeDone")
        val keyEnableNotifications = booleanPreferencesKey("enableNotifications")
        val keyListViewType = stringPreferencesKey("listViewType")
    }

    val settingsFlow: Flow<SettingsData> = ds.data
        .catch { _ -> emit(emptyPreferences()) }
        .map { preferences ->
            SettingsData(
                theme = ThemeSelect.valueOf(
                    preferences[keyTheme] ?: SettingsData.initial.theme.name
                ),
                welcomeDone = preferences[keyWelcomeDone] ?: SettingsData.initial.welcomeDone,
                enableNotifications = preferences[keyEnableNotifications]
                    ?: SettingsData.initial.enableNotifications,
                listViewType = ListViewType.valueOf(
                    preferences[keyListViewType] ?: SettingsData.initial.listViewType.name
                )
            )
        }

    suspend fun saveTheme(theme: ThemeSelect) {
        ds.edit { preferences -> preferences[keyTheme] = theme.name }
    }

    suspend fun saveWelcomeDone(done: Boolean) {
        ds.edit { preferences -> preferences[keyWelcomeDone] = done }
    }

    suspend fun saveEnableNotifications(enable: Boolean) {
        ds.edit { preferences -> preferences[keyEnableNotifications] = enable }
    }

    suspend fun saveListViewType(listViewType: ListViewType) {
        ds.edit { preferences -> preferences[keyListViewType] = listViewType.name }
    }

    suspend fun clear() {
        ds.edit { preferences -> preferences.clear() }
    }
}
