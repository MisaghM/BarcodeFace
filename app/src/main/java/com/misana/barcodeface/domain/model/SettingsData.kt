package com.misana.barcodeface.domain.model

data class SettingsData(
    val theme: ThemeSelect,
    val welcomeDone: Boolean,
    val enableNotifications: Boolean,
    val listViewType: ListViewType
) {
    companion object {
        val initial = SettingsData(
            theme = ThemeSelect.SYSTEM,
            welcomeDone = false,
            enableNotifications = false,
            listViewType = ListViewType.GRID
        )
    }
}
