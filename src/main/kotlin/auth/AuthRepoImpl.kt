package com.example.auth

import com.example.auth.model.RegisterRequest
import com.example.auth.model.User
import com.example.tables.Users
import com.example.util.UserRole
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.security.MessageDigest

class AuthRepoImpl : AuthRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    override suspend fun findUserByUsername(username: String): User? = suspendTransaction {
        Users.selectAll()
            .where { Users.username eq username }
            .singleOrNull()
            ?.let { row ->
                User(
                    id = row[Users.id],
                    username = row[Users.username],
                    email = row[Users.email],
                    passwordHash = row[Users.passwordHash],
                    firstName = row[Users.firstName],
                    lastName = row[Users.lastName],
                    role = UserRole.valueOf(row[Users.role])
                )
            }
    }

    override suspend fun createUser(request: RegisterRequest): User? = suspendTransaction {
        try {
            val userId = Users.insert {
                it[username] = request.username
                it[email] = request.email
                it[passwordHash] = hashPassword(request.password)
                it[firstName] = request.firstName
                it[lastName] = request.lastName
                it[role] = request.role.name
            }[Users.id]

            User(
                id = userId,
                username = request.username,
                email = request.email,
                passwordHash = hashPassword(request.password),
                firstName = request.firstName,
                lastName = request.lastName,
                role = request.role
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun validateCredentials(username: String, password: String): User? = suspendTransaction {
        val hashedPassword = hashPassword(password)
        Users.selectAll()
            .where { (Users.username eq username) and (Users.passwordHash eq hashedPassword) }
            .singleOrNull()
            ?.let { row ->
                User(
                    id = row[Users.id],
                    username = row[Users.username],
                    email = row[Users.email],
                    passwordHash = row[Users.passwordHash],
                    firstName = row[Users.firstName],
                    lastName = row[Users.lastName],
                    role = UserRole.valueOf(row[Users.role])
                )
            }
    }
}