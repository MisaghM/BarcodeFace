package com.misana.barcodeface.domain.model

data class NetworkResult<T>(
    val status: Status,
    val code: Int = -1,
    val body: T? = null,
    val error: Throwable? = null
) {
    enum class Status {
        SUCCESS,
        ERROR,
        TIMEOUT,
        RESET,
        OTHER
    }

    companion object {
        fun <T> message(netRes: NetworkResult<T>): String {
            return when (netRes.status) {
                Status.SUCCESS -> "Success"
                Status.ERROR -> "Server error occurred"
                Status.TIMEOUT -> "Connection timed out"
                Status.RESET -> "Connection reset (no internet?)"
                Status.OTHER -> "Unknown error occurred"
            }
        }
    }
}
