package com.misana.barcodeface.data.repository

import com.haroldadmin.cnradapter.NetworkResponse
import com.misana.barcodeface.data.remote.FerApi
import com.misana.barcodeface.domain.model.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.SocketException
import java.net.SocketTimeoutException

class FerRepository(
    private val ferApi: FerApi
) {
    suspend fun getHealth(): NetworkResult<Boolean> {
        return createNetworkResult(
            resp = ferApi.getHealth(),
            bodyOnSuccess = { ferDto ->
                ferDto.data == "healthy"
            },
            bodyOnError = false
        )
    }

    suspend fun detect(token: String, image: File): NetworkResult<String> {
        val tokenBody = token.toRequestBody(MultipartBody.FORM)
        val imageBody = image.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", image.name, imageBody)
        val resp = ferApi.detect(tokenBody, part)
        return createNetworkResult(
            resp = resp,
            bodyOnSuccess = { ferDto -> ferDto.data },
            bodyOnError = "none"
        )
    }

    companion object {
        fun <S, E, T> createNetworkResult(
            resp: NetworkResponse<S, E>,
            bodyOnSuccess: (S) -> T,
            bodyOnError: T?
        ): NetworkResult<T> {
            return when (resp) {
                is NetworkResponse.Success -> {
                    NetworkResult(
                        status = NetworkResult.Status.SUCCESS,
                        code = resp.code,
                        body = bodyOnSuccess(resp.body)
                    )
                }

                is NetworkResponse.ServerError -> {
                    NetworkResult(
                        status = NetworkResult.Status.ERROR,
                        code = resp.code!!,
                        body = bodyOnError,
                    )
                }

                is NetworkResponse.NetworkError -> {
                    when (resp.error) {
                        is SocketTimeoutException -> {
                            NetworkResult(
                                status = NetworkResult.Status.TIMEOUT,
                                error = resp.error
                            )
                        }

                        is SocketException -> {
                            NetworkResult(
                                status = NetworkResult.Status.RESET,
                                error = resp.error
                            )
                        }

                        else -> {
                            NetworkResult(
                                status = NetworkResult.Status.OTHER,
                                error = resp.error
                            )
                        }
                    }
                }

                is NetworkResponse.UnknownError -> {
                    NetworkResult(
                        status = NetworkResult.Status.OTHER,
                        error = resp.error
                    )
                }
            }
        }
    }
}
