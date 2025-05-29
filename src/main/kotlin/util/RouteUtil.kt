package com.example.util

import com.example.auth.JwtConfig
import com.example.auth.model.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.requireRole(vararg allowedRoles: UserRole): UserPrincipal? {
    val authHeader = request.headers["Authorization"]
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        respond(HttpStatusCode.Unauthorized, "Missing or invalid authorization header")
        return null
    }

    val token = authHeader.removePrefix("Bearer ")
    val userPrincipal = JwtConfig.verifyToken(token)

    if (userPrincipal == null) {
        respond(HttpStatusCode.Unauthorized, "Invalid token")
        return null
    }

    if (userPrincipal.role !in allowedRoles) {
        respond(HttpStatusCode.Forbidden, "Insufficient permissions")
        return null
    }

    return userPrincipal
}

fun ApplicationCall.getCurrentUser(): UserPrincipal? {
    val authHeader = request.headers["Authorization"]
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return null
    }

    val token = authHeader.removePrefix("Bearer ")
    return JwtConfig.verifyToken(token)
}