package com.example.visits

import com.example.employees.Employee
import com.example.employees.Specialty
import com.example.tables.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class VisitRepoImpl : VisitRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun getAllVisits(): List<Visit> = suspendTransaction {
        Visits.selectAll().map { row ->
            val discountId = row[Visits.discountId]
            val discount = Discounts.select(Discounts.id, Discounts.discountPercent)
                .where { Discounts.id eq discountId }
                .singleOrNull()?.let { discountRow ->
                    Discount(
                        id = discountRow[Discounts.id],
                        percent = discountRow[Discounts.id]
                    )
                } ?: throw NoSuchElementException("Discount with id $discountId not found")

            val patientId = row[Visits.patientId]
            val patient = Patients.select(Patients.columns)
                .where { Patients.id eq patientId }
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
                } ?: throw NoSuchElementException("Patient with id $patientId not found")

            val employeeId = row[Visits.employeeId]
            val employee = Employees.select(Employees.columns)
                .where { Employees.id eq employeeId }
                .singleOrNull()?.let { employeeRow ->
                    val specialityId = employeeRow[Employees.specialityId]
                    val speciality = Specialties.select(Specialties.id, Specialties.specialityName)
                        .where { Specialties.id eq specialityId }
                        .singleOrNull()?.let { specialityRow ->
                            Specialty(
                                id = specialityRow[Specialties.id],
                                name = specialityRow[Specialties.specialityName]
                            )
                        } ?: throw NoSuchElementException("Specialty with id $specialityId not found")

                    Employee(
                        id = employeeRow[Employees.id],
                        speciality = speciality,
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
                } ?: throw NoSuchElementException("Employee with id $employeeId not found")

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

    override suspend fun deleteVisit(id: Int): Unit = suspendTransaction {
        Visits.deleteWhere { Visits.id eq id }
    }
}