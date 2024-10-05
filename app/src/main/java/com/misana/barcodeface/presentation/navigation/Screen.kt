package com.misana.barcodeface.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Welcome : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen
}
