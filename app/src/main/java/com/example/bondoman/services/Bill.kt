package com.example.bondoman.services

import com.example.bondoman.models.BillReq
import com.example.bondoman.models.BillRes
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface Bill {
    @Multipart
    @POST("api/bill/upload")
    fun upload(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
    ): Call<BillRes>
}