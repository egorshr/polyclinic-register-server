package com.example.auth.model

import com.example.util.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val role: UserRole,
    val userId: Int
)