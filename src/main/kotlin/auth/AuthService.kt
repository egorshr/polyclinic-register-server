package com.example.auth

import com.example.auth.model.LoginRequest
import com.example.auth.model.LoginResponse
import com.example.auth.model.RegisterRequest
import com.example.auth.model.UserPrincipal


class AuthService(private val authRepository: AuthRepo) {

    suspend fun login(request: LoginRequest): LoginResponse? {
        val user = authRepository.validateCredentials(request.username, request.password)
            ?: return null

        val userPrincipal = UserPrincipal(
            id = user.id,
            username = user.username,
            role = user.role
        )

        val token = JwtConfig.generateToken(userPrincipal)

        return LoginResponse(
            token = token,
            role = user.role,
            userId = user.id
        )
    }

    suspend fun register(request: RegisterRequest): LoginResponse? {

        if (authRepository.findUserByUsername(request.username) != null) {
            return null
        }

        val user = authRepository.createUser(request) ?: return null

        val userPrincipal = UserPrincipal(
            id = user.id,
            username = user.username,
            role = user.role
        )

        val token = JwtConfig.generateToken(userPrincipal)

        return LoginResponse(
            token = token,
            role = user.role,
            userId = user.id
        )
    }
}