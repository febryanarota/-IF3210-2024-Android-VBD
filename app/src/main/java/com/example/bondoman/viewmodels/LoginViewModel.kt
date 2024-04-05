package com.example.bondoman.viewmodels

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
    val token = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                isLoading.value = true
//                val loginRequest = LoginReq("13521120@std.stei.itb.ac.id", "password_13521120")
                val loginRequest = LoginReq(email, password)
                val apiService = RetrofitInstance.auth

                apiService.login(loginRequest).enqueue(object : Callback<LoginRes> {
                    override fun onResponse(call: Call<LoginRes>, response: Response<LoginRes>) {
                        if (response.isSuccessful) {
                            token.value = response.body()?.token
                            isLoading.value = false
                        } else {
                            message.value = "Login failed ${response.message()}"
                            isLoading.value = false
                        }
                    }

                    override fun onFailure(call: Call<LoginRes>, t: Throwable) {
                        message.value = "Network error: ${t.message}"
                        isLoading.value = false
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
                message.value = "Network error: ${e.message}"
                isLoading.value = false
            }
        }
    }
}