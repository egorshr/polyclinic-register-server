package com.example

import com.example.employees.Employee
import com.example.employees.EmployeeRepo
import com.example.services.Service
import com.example.services.ServiceRepo
import com.example.visits.Visit
import com.example.visits.VisitRepo
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureSerialization(
    serviceRepo: ServiceRepo,
    employeeRepo: EmployeeRepo,
    visitRepo: VisitRepo
) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("services") {
            get {
                val services = serviceRepo.getAllServices()
                call.respond(services)
            }
            put{
                val service = call.receive<Service>()
                serviceRepo.updateService(service)
            }

        }

        route("employees") {
            get {
                val employees = employeeRepo.getAllEmployees()
                call.respond(employees)
            }
            put {
                val employee = call.receive<Employee>()
                employeeRepo.updateEmployee(employee)
            }
        }

        route("visits") {
            get {
                val visits = visitRepo.getAllVisits()
                call.respond(visits)
            }

            put {
                val visit = call.receive<Visit>()
                visitRepo.updateVisit(visit)
            }

        }
    }

}
