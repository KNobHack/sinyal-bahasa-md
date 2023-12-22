package com.example.sinyal.api

import com.example.sinyal.dataclass.EventDetail
import com.example.sinyal.dataclass.LoginAccount
import com.example.sinyal.dataclass.RegisterDataAccount
import com.example.sinyal.dataclass.ResponseDetail
import com.example.sinyal.dataclass.ResponseLogin
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @POST("register")
    fun registUser(@Body requestRegister: RegisterDataAccount): Call<ResponseDetail>

    @POST("login")
    fun loginUser(@Body requestLogin: LoginAccount): Call<ResponseLogin>

    @GET("stories")
    fun getLocationStory(
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = 0,
        @Header("Authorization") token: String,
    ): Call<EventDetail>

    @Multipart
    @POST("stories")
    fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
        @Header("Authorization") token: String
    ): Call<ResponseDetail>
}