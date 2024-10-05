package com.misana.barcodeface.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Barcode(
    val content: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = 0
)
