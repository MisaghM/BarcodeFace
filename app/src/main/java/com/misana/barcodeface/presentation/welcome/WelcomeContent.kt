package com.misana.barcodeface.presentation.welcome

import androidx.annotation.DrawableRes
import com.misana.barcodeface.R

data class WelcomeContent(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    companion object {
        val pages = listOf(
            WelcomeContent(
                title = "BarcodeFace",
                description = "Welcome to BarcodeFace!",
                image = R.drawable.logo
            ),
            WelcomeContent(
                title = "Facial Expressions",
                description = "BarcodeFace detects your facial expressions and saves the result as a barcode.",
                image = R.drawable.fer
            ),
            WelcomeContent(
                title = "Scan Barcodes",
                description = "The saved barcodes are then combined to create an estimate of your mental state.",
                image = R.drawable.barcode
            )
        )
    }
}
