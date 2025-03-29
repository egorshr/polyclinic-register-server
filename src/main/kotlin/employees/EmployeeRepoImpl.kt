package com.example.employees

import com.example.tables.Employees
import com.example.tables.Specialties
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class EmployeeRepoImpl : EmployeeRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun getAllEmployees(): List<Employee> = suspendTransaction {
        Employees.selectAll().map { employeeRow ->
            val specialityId = employeeRow[Employees.specialityId]
            val speciality = Specialties.selectAll().where { Specialties.id eq specialityId }.singleOrNull()
                ?.let { specialityRow ->
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
        }
    }


    override suspend fun updateEmployee(employee: Employee): Unit = suspendTransaction {
        try {
            Employees.update({ Employees.id eq employee.id }) {
                it[Employees.specialityId] = employee.speciality.id
                it[Employees.employeeFirstName] = employee.firstName
                it[Employees.employeeMiddleName] = employee.middleName
                it[Employees.employeeLastName] = employee.lastName
                it[Employees.employeeBirthday] = employee.birthday
                it[Employees.employeeGender] = employee.gender
                it[Employees.employeeJobTitle] = employee.jobTitle
                it[Employees.employeePhoneNumber] = employee.phoneNumber
                it[Employees.employeeDurationOfVisit] = employee.durationOfVisit
                it[Employees.employeeUsername] = employee.username
                it[Employees.employeeEmail] = employee.email
                it[Employees.employeePassword] = employee.password
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteEmployee(id: Int): Unit = suspendTransaction {
        try {
            Employees.deleteWhere { Employees.id eq id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}