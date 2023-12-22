package com.example.sinyal.dataclass


import com.google.gson.annotations.SerializedName

data class Response(

    @field:SerializedName("prediction")
    val prediction: String? = null
)
