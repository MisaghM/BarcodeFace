package com.misana.barcodeface.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.misana.barcodeface.presentation.drawer.AppNavDrawer
import com.misana.barcodeface.presentation.drawer.DrawerItem
import com.misana.barcodeface.presentation.drawer.DrawerViewModel
import com.misana.barcodeface.presentation.home.HomeScreen
import com.misana.barcodeface.presentation.home.HomeViewModel
import com.misana.barcodeface.presentation.settings.SettingsScreen
import com.misana.barcodeface.presentation.settings.SettingsViewModel
import com.misana.barcodeface.presentation.welcome.WelcomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    settingsVm: SettingsViewModel,
    drawerVm: DrawerViewModel,
    homeVm: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = settingsVm.initialScreen
    ) {
        composable<Screen.Welcome>(
            enterTransition = { scaleInAnimation },
            exitTransition = { welcomeExitAnimation }
        ) {
            WelcomeScreen(
                onFinish = {
                    settingsVm.setWelcomeCompleted()
                    navController.popBackStack()
                    navController.navigate(Screen.Home)
                }
            )
        }
        composable<Screen.Home>(
            enterTransition = { scaleInAnimation },
            popEnterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            val uiScope = rememberCoroutineScope()
            AppNavDrawer(
                navController = navController,
                drawerVm = drawerVm
            ) {
                HomeScreen(
                    homeVm = homeVm,
                    settingsVm = settingsVm,
                    onDrawerClick = {
                        drawerVm.switchDrawer(uiScope)
                    },
                )
            }
        }
        composable<Screen.Settings>(
            enterTransition = { slideInAnimation },
            exitTransition = { slideOutAnimation }
        ) {
            SettingsScreen(
                settingsVm = settingsVm,
                onScreenClose = { onSettingsClose(drawerVm, navController) }
            )
            BackHandler { onSettingsClose(drawerVm, navController) }
        }
    }
}

private fun onSettingsClose(
    drawerVm: DrawerViewModel,
    navController: NavHostController
) {
    drawerVm.setDrawerSelect(DrawerItem.Index.HOME)
    navController.popBackStack()
}
