package com.example.database

import com.example.tables.Discounts
import com.example.tables.Employees
import com.example.tables.Patients
import com.example.tables.Schedules
import com.example.tables.Services
import com.example.tables.SocialStatuses
import com.example.tables.Specialties
import com.example.tables.Users
import com.example.tables.Visits
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabases() {
    val url = environment.config.property("storage.jdbcURL").getString()
    val user = environment.config.property("storage.user").getString()
    val password = environment.config.property("storage.password").getString()
    val driver = environment.config.property("storage.driverClassName").getString()
    Database.connect(
        url = url,
        user = user,
        password = password,
        driver = driver
    )
    transaction {
        SchemaUtils.create(
            Discounts,
            Employees,
            Patients,
            Schedules,
            Services,
            SocialStatuses,
            Specialties,
            Visits,
            Users
        )
    }
}