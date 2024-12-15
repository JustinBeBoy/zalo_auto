package com.example.quick_reply.data.entity

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("birth_day")
    val birthDay: String? = null,
    @SerializedName("confirmed_at")
    val confirmedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("last_seen")
    val lastSeen: String? = null,
    @SerializedName("license")
    val license: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("signature")
    val signature: String? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("user_name")
    val userName: String? = null
)