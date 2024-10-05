package com.misana.barcodeface.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorDto(
    val timestamp: Long,
    val error: String
)
