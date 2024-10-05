package com.misana.barcodeface.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FerDto(
    val timestamp: Long,
    val data: String
)
