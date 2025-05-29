package com.example.plugins

import com.auth0.jwt.JWT
import com.example.auth.JwtConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier { token ->
                JwtConfig.verifyToken(token)?.let {
                    JWTPrincipal(
                        payload = JWT.decode(token)
                    )
                }
            }
            validate { credential ->
                val token = credential.payload.token
                JwtConfig.verifyToken(token)?.let { userPrincipal ->
                    UserIdPrincipal(userPrincipal.username)
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}