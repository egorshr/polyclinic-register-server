package com.example.auth.model

import com.example.util.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class UserPrincipal(
    val id: Int,
    val username: String,
    val role: UserRole
)