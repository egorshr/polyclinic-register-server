package com.example.util

import com.example.auth.JwtConfig
import com.example.auth.model.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

suspend fun ApplicationCall.requireRole(vararg allowedRoles: UserRole): UserPrincipal? {
    val principal = principal<JWTPrincipal>()
    if (principal == null) {
        respond(HttpStatusCode.Unauthorized, "Missing or invalid authorization")
        return null
    }

    val userPrincipal = UserPrincipal(
        id = principal.payload.subject.toInt(),
        username = principal.payload.getClaim("username").asString(),
        role = UserRole.valueOf(principal.payload.getClaim("role").asString())
    )

    if (userPrincipal.role !in allowedRoles) {
        respond(HttpStatusCode.Forbidden, "Insufficient permissions")
        return null
    }

    return userPrincipal
}

fun ApplicationCall.getCurrentUser(): UserPrincipal? {
    val principal = principal<JWTPrincipal>() ?: return null

    return try {
        UserPrincipal(
            id = principal.payload.subject.toInt(),
            username = principal.payload.getClaim("username").asString(),
            role = UserRole.valueOf(principal.payload.getClaim("role").asString())
        )
    } catch (e: Exception) {
        null
    }
}

// Альтернативная версия getCurrentUser() для случаев без аутентификации
fun ApplicationCall.getCurrentUserFromHeader(): UserPrincipal? {
    val authHeader = request.headers["Authorization"]
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return null
    }

    val token = authHeader.removePrefix("Bearer ")
    return JwtConfig.verifyToken(token)
}