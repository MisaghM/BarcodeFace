package com.misana.barcodeface.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misana.barcodeface.data.repository.FerRepository
import com.misana.barcodeface.data.repository.SettingsRepository
import com.misana.barcodeface.domain.model.ListViewType
import com.misana.barcodeface.domain.model.NetworkResult
import com.misana.barcodeface.domain.model.SettingsData
import com.misana.barcodeface.domain.model.ThemeSelect
import com.misana.barcodeface.presentation.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepo: SettingsRepository,
    private val ferRepo: FerRepository
) : ViewModel() {
    var initialized: Boolean by mutableStateOf(false)
        private set

    var settings: SettingsData by mutableStateOf(SettingsData.initial)
        private set

    var initialScreen: Screen by mutableStateOf(Screen.Welcome)
        private set

    private var _resetRequest = Channel<Boolean>()
    val resetRequest = _resetRequest.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            setInitialSettings()
        }
    }

    private suspend fun setInitialSettings() {
        val settingsData = settingsRepo.settingsFlow.first()
        initialized = false
        settings = settingsData
        initialScreen = if (settings.welcomeDone) Screen.Home else Screen.Welcome
        initialized = true
    }

    fun setWelcomeCompleted() {
        settings = settings.copy(welcomeDone = true)
        initialScreen = Screen.Home
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.saveWelcomeDone(true)
        }
    }

    fun setThemeScheme(themeSelect: ThemeSelect) {
        settings = settings.copy(theme = themeSelect)
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.saveTheme(themeSelect)
        }
    }

    fun setNotifications(enabled: Boolean) {
        settings = settings.copy(enableNotifications = enabled)
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.saveEnableNotifications(enabled)
        }
    }

    fun setListViewType(type: ListViewType) {
        settings = settings.copy(listViewType = type)
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.saveListViewType(type)
        }
    }

    fun setAsUninitialized() {
        initialized = false
    }

    fun reset() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepo.clear()
            setInitialSettings()
        }
    }

    fun sendResetRequest() {
        viewModelScope.launch {
            _resetRequest.send(true)
        }
    }

    suspend fun checkServerConnectivity(): Boolean {
        val result = ferRepo.getHealth()
        return result.status == NetworkResult.Status.SUCCESS
    }

    class Factory(
        private val settingsRepo: SettingsRepository,
        private val ferRepo: FerRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepo, ferRepo) as T
        }
    }
}
