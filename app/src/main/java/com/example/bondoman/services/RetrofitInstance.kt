package com.example.bondoman.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // retrofit instance object class
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: Auth by lazy {
        retrofit.create(Auth::class.java)
    }
}