package com.misana.barcodeface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.misana.barcodeface.di.MainApplication.Companion.appModule
import com.misana.barcodeface.presentation.drawer.DrawerViewModel
import com.misana.barcodeface.presentation.home.HomeViewModel
import com.misana.barcodeface.presentation.navigation.AppNavigation
import com.misana.barcodeface.presentation.settings.SettingsViewModel
import com.misana.barcodeface.ui.theme.BarcodeFaceTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val settingsVm: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(appModule.settingsRepo, appModule.ferRepo)
    }
    private val drawerVm: DrawerViewModel by viewModels()
    private val homeVm: HomeViewModel by viewModels {
        HomeViewModel.Factory(appModule.barcodeService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !settingsVm.initialized }
        enableEdgeToEdge()
        bindRequests()

        setContent {
            val navController = rememberNavController()
            if (settingsVm.initialized) {
                BarcodeFaceTheme(settingsVm.settings.theme) {
                    AppNavigation(navController, settingsVm, drawerVm, homeVm)
                }
            }
        }
    }

    private fun bindRequests() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch {
                drawerVm.exitRequest.collect { shouldExit ->
                    if (shouldExit) {
                        this@MainActivity.finishAndRemoveTask()
                    }
                }
            }
            launch(Dispatchers.IO) {
                settingsVm.resetRequest.collect { shouldReset ->
                    if (shouldReset) {
                        this@MainActivity.resetData()
                    }
                }
            }
        }
    }

    private fun resetData() {
        settingsVm.setAsUninitialized()
        homeVm.reset()
        drawerVm.reset()
        appModule.clearDatabase()
        cacheDir.deleteRecursively()
        settingsVm.reset()
    }
}
