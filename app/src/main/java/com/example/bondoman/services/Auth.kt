package com.example.bondoman.services

import com.example.bondoman.models.LoginReq
import com.example.bondoman.models.LoginRes
import com.example.bondoman.models.TokenRes
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface Auth {
    @POST("api/auth/login")
    fun login(@Body request: LoginReq): Call<LoginRes>

    @POST("api/auth/token")
    fun checkToken(@Header("Authorization") token: String) : Call<TokenRes>

}