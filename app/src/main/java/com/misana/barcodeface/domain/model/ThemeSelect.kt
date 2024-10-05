package com.misana.barcodeface.domain.model

enum class ThemeSelect {
    LIGHT,
    DARK,
    SYSTEM;

    override fun toString() = when (this) {
        LIGHT -> "Light"
        DARK -> "Dark"
        SYSTEM -> "System"
    }
}
