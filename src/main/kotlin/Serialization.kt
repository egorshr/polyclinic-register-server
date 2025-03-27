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
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Instant

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
                val updatedService = serviceRepo.getAllServices().find { it.id == service.id }!!
                call.respond(updatedService)
            }
            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                serviceRepo.deleteService(id)
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
            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                employeeRepo.deleteEmployee(id)
            }
        }

        route("visits") {
            get {
                try {
                    val startDateParam = call.request.queryParameters["startDate"]
                    val endDateParam = call.request.queryParameters["endDate"]

                    val startDate = startDateParam?.let { Instant.parse(it) }
                    val endDate = endDateParam?.let { Instant.parse(it) }

                    println("Received params: startDate=$startDateParam, endDate=$endDateParam")

                    val visits = visitRepo.getAllVisits(startDate, endDate)
                    call.respond(visits)
                } catch (e: Exception) {
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                }
            }

            put {
                val visit = call.receive<Visit>()
                visitRepo.updateVisit(visit)
            }

            delete("/{id}") {
                val id = call.parameters["id"]!!.toInt()
                visitRepo.deleteVisit(id)
            }

        }
    }

}
