package com.example.quick_reply.data.remote.error

enum class ErrorType(
    val code: Int,
    var message: String
) {
    NETWORK(0x111, "Network Error"),
    DATA(0x222, "Data Error"),
    HTTP(0x333, "HTTP Error"),
    UNKNOWN(0x999, "Unknown error")
}