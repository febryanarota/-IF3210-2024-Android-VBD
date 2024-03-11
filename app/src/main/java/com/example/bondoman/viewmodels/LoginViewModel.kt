package com.example.bondoman.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bondoman.models.LoginReq
import com.example.bondoman.models.LoginRes
import com.example.bondoman.services.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    val loginResult = MutableLiveData<Boolean>()
    val message = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
//                val loginRequest = LoginReq("13521120@std.stei.itb.ac.id", "password_13521120")
                val loginRequest = LoginReq(email, password)
                val apiService = RetrofitInstance.auth

                apiService.login(loginRequest).enqueue(object : Callback<LoginRes> {
                    override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                        if (response.isSuccessful) {
                            // to do store the token
                            val token = response.body()?.token
                            loginResult.value = true
                        } else {
                            message.value = "Login failed ${response.message()}"
                        }
                    }

                    override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                        message.value = "Network error: ${t.message}"
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                message.value = "Network error: ${e.message}"
            }
        }
    }
}