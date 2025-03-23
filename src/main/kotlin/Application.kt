package com.example

import com.example.database.configureDatabases
import com.example.employees.EmployeeRepo
import com.example.employees.EmployeeRepoImpl
import com.example.services.ServiceRepoImpl
import com.example.visits.VisitRepoImpl
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val serviceRepository = ServiceRepoImpl()
    val employeeRepo = EmployeeRepoImpl()
    val visitRepo = VisitRepoImpl()
    configureSerialization(serviceRepository, employeeRepo, visitRepo)
    configureDatabases()
    configureFrameworks()
    configureRouting()
}
