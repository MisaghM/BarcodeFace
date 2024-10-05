package com.misana.barcodeface.domain.model

enum class ListViewType {
    GRID,
    LIST;

    override fun toString() = when (this) {
        GRID -> "Grid"
        LIST -> "List"
    }
}
