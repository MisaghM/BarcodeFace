package com.misana.barcodeface.data.remote

import androidx.annotation.Keep
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

@Keep
interface FerApi {
    @GET("/api/health")
    suspend fun getHealth(): NetworkResponse<FerDto, ErrorDto>

    @Multipart
    @POST("/api/detect")
    suspend fun detect(
        @Part("token") token: RequestBody,
        @Part image: MultipartBody.Part
    ): NetworkResponse<FerDto, String>

    companion object {
        const val BASE_URL = "http://127.0.0.1:8000/"
    }
}
