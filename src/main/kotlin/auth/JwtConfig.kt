package com.example.auth

import com.example.auth.model.UserPrincipal
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.util.UserRole
import java.util.Date


object JwtConfig {
    private const val SECRET = "your-secret-key-change-in-production"
    private const val ISSUER = "poliklinika-app"
    private const val AUDIENCE = "poliklinika-users"
    private const val VALIDITY_IN_MS = 36_000_00 * 24

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(user: UserPrincipal): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(user.id.toString())
            .withClaim("username", user.username)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(algorithm)
    }

    fun getVerifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .build()
    }

    fun verifyToken(token: String): UserPrincipal? {
        return try {
            val verifier = getVerifier()
            val jwt = verifier.verify(token)
            UserPrincipal(
                id = jwt.subject.toInt(),
                username = jwt.getClaim("username").asString(),
                role = UserRole.valueOf(jwt.getClaim("role").asString())
            )
        } catch (e: JWTVerificationException) {
            null
        }
    }
}