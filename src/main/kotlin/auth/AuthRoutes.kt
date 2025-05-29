package com.example.auth

import com.example.auth.model.LoginRequest
import com.example.auth.model.RegisterRequest
import com.example.util.getCurrentUser
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("auth") {
        post("login") {
            try {
                val request = call.receive<LoginRequest>()
                val response = authService.login(request)

                if (response != null) {
                    call.respond(HttpStatusCode.OK, response)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request format"))
            }
        }

        post("register") {
            try {
                val request = call.receive<RegisterRequest>()
                val response = authService.register(request)

                if (response != null) {
                    call.respond(HttpStatusCode.Created, response)
                } else {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to "User already exists"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request format"))
            }
        }

        get("profile") {
            val user = call.getCurrentUser()
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
            }
        }
    }
}