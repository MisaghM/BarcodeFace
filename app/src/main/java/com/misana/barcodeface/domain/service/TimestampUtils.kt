package com.misana.barcodeface.domain.service

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm"

fun Long.toLocalTimeString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT)
    return localDateTime.format(formatter)
}

fun Long.isBeforeMinutes(minutes: Long): Boolean {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return localDateTime.isBefore(LocalDateTime.now().minusMinutes(minutes))
}