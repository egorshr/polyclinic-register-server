package com.example

import com.example.database.configureDatabases
import com.example.employees.EmployeeRepoImpl
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
    configureSerialization()
    configureDatabases()
    configureFrameworks()
    configureRouting(serviceRepo, employeeRepo, visitRepo)
}
