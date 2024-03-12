package com.example.bondoman.services

import com.example.bondoman.models.LoginReq
import com.example.bondoman.models.LoginRes
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Auth {
    @POST("api/auth/login")
    fun login(@Body request: LoginReq): Call<LoginRes>
}