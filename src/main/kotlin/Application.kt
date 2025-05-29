package com.example

import com.example.auth.AuthRepoImpl
import com.example.auth.AuthService
import com.example.database.configureDatabases
import com.example.employees.EmployeeRepoImpl
import com.example.plugins.configureAuthentication
import com.example.plugins.configureFrameworks
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.services.ServiceRepoImpl
import com.example.visits.VisitRepoImpl
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val serviceRepo = ServiceRepoImpl()
    val employeeRepo = EmployeeRepoImpl()
    val visitRepo = VisitRepoImpl()
    val authRepo = AuthRepoImpl()
    val authService = AuthService(authRepo)
    configureSerialization()
    configureDatabases()
    configureFrameworks()
    configureAuthentication()
    configureRouting(serviceRepo, employeeRepo, visitRepo, authService)
}
