package com.example.quick_reply.data.remote

import com.example.quick_reply.data.entity.UserInfo
import com.example.quick_reply.data.entity.request.RegisterRequest
import com.example.quick_reply.data.entity.response.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserInfo>
}