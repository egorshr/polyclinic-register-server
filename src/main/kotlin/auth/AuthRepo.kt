package com.example.auth

import com.example.auth.model.RegisterRequest
import com.example.auth.model.User

interface AuthRepo {
    suspend fun findUserByUsername(username: String): User?
    suspend fun createUser(request: RegisterRequest): User?
    suspend fun validateCredentials(username: String, password: String): User?
}