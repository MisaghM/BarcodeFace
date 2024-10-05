package com.misana.barcodeface.presentation.home

import androidx.annotation.DrawableRes
import com.misana.barcodeface.R
import com.misana.barcodeface.domain.model.ListViewType

data class ListViewMenuItem(
    val listViewType: ListViewType,
    @DrawableRes
    val icon: Int
) {
    companion object {
        val items = listOf(
            ListViewMenuItem(
                listViewType = ListViewType.GRID,
                icon = R.drawable.grid_on
            ),
            ListViewMenuItem(
                listViewType = ListViewType.LIST,
                icon = R.drawable.view_list
            )
        )
    }
}
