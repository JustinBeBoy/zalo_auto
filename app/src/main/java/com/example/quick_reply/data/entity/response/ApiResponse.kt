package com.example.quick_reply.data.entity.response

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("data")
    val data: T? = null
)