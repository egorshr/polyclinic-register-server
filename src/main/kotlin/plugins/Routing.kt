package com.example.plugins

import com.example.auth.AuthService
import com.example.auth.authRoutes
import com.example.employees.Employee
import com.example.employees.EmployeeRepo
import com.example.services.Service
import com.example.services.ServiceRepo
import com.example.util.UserRole
import com.example.util.getCurrentUser
import com.example.util.requireRole
import com.example.visits.Visit
import com.example.visits.VisitRepo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant

fun Application.configureRouting(
    serviceRepo: ServiceRepo,
    employeeRepo: EmployeeRepo,
    visitRepo: VisitRepo,
    authService: AuthService
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        // Публичные роуты (без аутентификации)
        authRoutes(authService)

        // Защищенные роуты (требуют аутентификации)
        authenticate("auth-jwt") {
            route("services") {
                // Просмотр услуг - доступно всем авторизованным пользователям
                get {
                    try {
                        val order = call.request.queryParameters["order"] ?: "asc"
                        val services = serviceRepo.getAllServices(order)
                        call.respond(services)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Создание услуги - только для ADMIN и REGISTRAR
                post {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR)
                    if (user == null) return@post

                    try {
                        val service = call.receive<Service>()
                        serviceRepo.createService(service)
                        call.respond(HttpStatusCode.Created, service)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Обновление услуги - только для ADMIN и REGISTRAR
                put {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR)
                    if (user == null) return@put

                    try {
                        val service = call.receive<Service>()
                        serviceRepo.updateService(service)
                        val updatedService = serviceRepo.getAllServices().find { it.id == service.id }!!
                        call.respond(updatedService)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Удаление услуги - только для ADMIN
                delete("/{id}") {
                    val user = call.requireRole(UserRole.ADMIN)
                    if (user == null) return@delete

                    try {
                        val id = call.parameters["id"]!!.toInt()
                        serviceRepo.deleteService(id)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }
            }

            // Employees routes
            route("employees") {
                // Просмотр сотрудников - ADMIN, REGISTRAR, DOCTOR
                get {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR, UserRole.DOCTOR)
                    if (user == null) return@get

                    try {
                        val employees = employeeRepo.getAllEmployees()
                        call.respond(employees)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Обновление сотрудника - только ADMIN
                put {
                    val user = call.requireRole(UserRole.ADMIN)
                    if (user == null) return@put

                    try {
                        val employee = call.receive<Employee>()
                        employeeRepo.updateEmployee(employee)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Удаление сотрудника - только ADMIN
                delete("/{id}") {
                    val user = call.requireRole(UserRole.ADMIN)
                    if (user == null) return@delete

                    try {
                        val id = call.parameters["id"]!!.toInt()
                        employeeRepo.deleteEmployee(id)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }
            }

            // Visits routes
            route("visits") {
                // Просмотр визитов
                get {
                    val user = call.getCurrentUser()
                    if (user == null) {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Authentication required"))
                        return@get
                    }

                    try {
                        val startDateParam = call.request.queryParameters["startDate"]
                        val endDateParam = call.request.queryParameters["endDate"]

                        val startDate = startDateParam?.let { Instant.parse(it) }
                        val endDate = endDateParam?.let { Instant.parse(it) }

                        val visits = when (user.role) {
                            UserRole.PATIENT -> {
                                // Пациент видит только свои визиты
                                visitRepo.getVisitsByPatientId(user.id, startDate, endDate)
                            }
                            UserRole.DOCTOR -> {
                                // Доктор видит визиты к себе
                                visitRepo.getVisitsByDoctorId(user.id, startDate, endDate)
                            }
                            UserRole.REGISTRAR, UserRole.ADMIN -> {
                                // Регистратор и админ видят все визиты
                                visitRepo.getAllVisits(startDate, endDate)
                            }
                        }
                        call.respond(visits)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Создание визита - REGISTRAR, ADMIN, PATIENT (для себя)
                post {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR, UserRole.PATIENT)
                    if (user == null) return@post

                    try {
                        val visit = call.receive<Visit>()

                        // Пациент может создавать визиты только для себя
                        if (user.role == UserRole.PATIENT && visit.patient.id != user.id) {
                            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Can only create visits for yourself"))
                            return@post
                        }

                        visitRepo.createVisit(visit)
                        call.respond(HttpStatusCode.Created, visit)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Обновление визита - REGISTRAR, ADMIN
                put {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR)
                    if (user == null) return@put

                    try {
                        val visit = call.receive<Visit>()
                        visitRepo.updateVisit(visit)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }

                // Удаление визитов - ADMIN, REGISTRAR
                delete("/{id}") {
                    val user = call.requireRole(UserRole.ADMIN, UserRole.REGISTRAR)
                    if (user == null) return@delete

                    try {
                        val id = call.receive<Set<Int>>()
                        visitRepo.deleteVisit(id)
                        call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        call.respond(HttpStatusCode.InternalServerError, "Internal server error: ${e.localizedMessage}")
                    }
                }
            }
        }
    }
}