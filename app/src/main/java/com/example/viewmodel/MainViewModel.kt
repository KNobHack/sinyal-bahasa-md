package com.example.sinyal.viewmodel

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sinyal.LoginActivity
import com.example.sinyal.MainActivity
import com.example.sinyal.dataclass.LoginAccount

import com.example.sinyal.dataclass.RegisterDataAccount
import com.example.sinyal.dataclass.ResponseLogin
import com.example.sinyal.repository.MainRepository
import retrofit2.Response


class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {


    val message: LiveData<String> = mainRepository.message

    val isLoading: LiveData<Boolean> = mainRepository.isLoading

    val userlogin: LiveData<ResponseLogin> = mainRepository.userlogin

    fun login(loginDataAccount: LoginAccount,
              onResponse: (Response<ResponseLogin>) -> Unit,
              onFailure: (Throwable) -> Unit) {

        mainRepository.getResponseLogin(loginDataAccount, onResponse, onFailure)

    }

    fun register(registDataUser: RegisterDataAccount) {
        mainRepository.getResponseRegister(registDataUser)
    }


}
