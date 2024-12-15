package com.example.quick_reply.data.remote.error

data class ApiError(
    override val message: String,
    val code: Int
) : Throwable(message)