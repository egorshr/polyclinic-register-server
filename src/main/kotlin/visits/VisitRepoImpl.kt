package com.example.visits

import com.example.employees.Employee
import com.example.employees.Specialty
import com.example.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class VisitRepoImpl : VisitRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun getAllVisits(startDate: Instant?, endDate: Instant?): List<Visit> = suspendTransaction {
        val conditions = mutableListOf<Op<Boolean>>()

        if (startDate != null) {
            conditions += Visits.visitDateAndTime greaterEq startDate
        }

        if (endDate != null) {
            conditions += Visits.visitDateAndTime lessEq endDate
        }

        Visits.selectAll().apply {
            if (conditions.isNotEmpty()) {
                adjustWhere { conditions.reduce { acc, op -> acc and op } }
            }
        }.map { row ->
            val discount = Discounts.selectAll()
                .where { Discounts.id eq row[Visits.discountId] }
                .singleOrNull()?.let { discountRow ->
                    Discount(
                        id = discountRow[Discounts.id],
                        percent = discountRow[Discounts.discountPercent]
                    )
                } ?: throw NoSuchElementException("Discount not found")

            val patient = Patients.selectAll()
                .where { Patients.id eq row[Visits.patientId] }
                .singleOrNull()?.let { patientRow ->
                    Patient(
                        id = patientRow[Patients.id],
                        gender = patientRow[Patients.patientGender],
                        firstName = patientRow[Patients.patientFirstName],
                        middleName = patientRow[Patients.patientMiddleName],
                        lastName = patientRow[Patients.patientLastName],
                        birthday = patientRow[Patients.patientBirthday],
                        phoneNumber = patientRow[Patients.patientPhoneNumber],
                        passportSeries = patientRow[Patients.patientPassportSeries],
                        passportIssueDate = patientRow[Patients.patientPassportIssueDate],
                        passportIssuedBy = patientRow[Patients.patientPassportIssuedBy],
                        addressCountry = patientRow[Patients.patientAddressCountry],
                        addressRegion = patientRow[Patients.patientAddressRegion],
                        addressLocality = patientRow[Patients.patientAddressLocality],
                        addressStreet = patientRow[Patients.patientAddressStreet],
                        addressHouse = patientRow[Patients.patientAddressHouse],
                        addressBody = patientRow[Patients.patientAddressBody],
                        addressApartment = patientRow[Patients.patientAddressApartment]
                    )
                } ?: throw NoSuchElementException("Patient not found")

            val employee = Employees.selectAll()
                .where { Employees.id eq row[Visits.employeeId] }
                .singleOrNull()?.let { employeeRow ->
                    val specialty = Specialties.selectAll()
                        .where { Specialties.id eq employeeRow[Employees.specialityId] }
                        .singleOrNull()?.let { specialityRow ->
                            Specialty(
                                id = specialityRow[Specialties.id],
                                name = specialityRow[Specialties.specialityName]
                            )
                        } ?: throw NoSuchElementException("Specialty not found")

                    Employee(
                        id = employeeRow[Employees.id],
                        speciality = specialty,
                        firstName = employeeRow[Employees.employeeFirstName],
                        middleName = employeeRow[Employees.employeeMiddleName],
                        lastName = employeeRow[Employees.employeeLastName],
                        birthday = employeeRow[Employees.employeeBirthday],
                        gender = employeeRow[Employees.employeeGender],
                        jobTitle = employeeRow[Employees.employeeJobTitle],
                        phoneNumber = employeeRow[Employees.employeePhoneNumber],
                        durationOfVisit = employeeRow[Employees.employeeDurationOfVisit],
                        username = employeeRow[Employees.employeeUsername],
                        email = employeeRow[Employees.employeeEmail],
                        password = employeeRow[Employees.employeePassword],
                    )
                } ?: throw NoSuchElementException("Employee not found")

            Visit(
                id = row[Visits.id],
                discount = discount,
                patient = patient,
                employee = employee,
                dateAndTime = row[Visits.visitDateAndTime]
            )
        }
    }


    override suspend fun updateVisit(visit: Visit): Unit = suspendTransaction {
        Visits.update({ Visits.id eq visit.id }) {
            it[Visits.discountId] = visit.discount.id
            it[Visits.patientId] = visit.patient.id
            it[Visits.employeeId] = visit.employee.id
            it[Visits.visitDateAndTime] = visit.dateAndTime
        }
    }

    override suspend fun deleteVisit(id: Set<Int>): Unit = suspendTransaction {
        if (id.isNotEmpty()) {
            Visits.deleteWhere { Visits.id inList id }
        }
    }
}