package com.example.employees

import com.example.tables.Employees
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
        Employees.selectAll().map { row ->
            Employee(
                id = row[Employees.id],
                specialityId = row[Employees.specialityId],
                firstName = row[Employees.employeeFirstName],
                middleName = row[Employees.employeeMiddleName],
                lastName = row[Employees.employeeLastName],
                birthday = row[Employees.employeeBirthday],
                gender = row[Employees.employeeGender],
                jobTitle = row[Employees.employeeJobTitle],
                phoneNumber = row[Employees.employeePhoneNumber],
                durationOfVisit = row[Employees.employeeDurationOfVisit],
                username = row[Employees.employeeUsername],
                email = row[Employees.employeeEmail],
                password = row[Employees.employeePassword],
            )
        }
    }


    override suspend fun updateEmployee(employee: Employee): Unit = suspendTransaction {
        Employees.update({ Employees.id eq employee.id }) {
            it[Employees.specialityId] = employee.specialityId
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
    }

    override suspend fun deleteEmployee(id: Int): Unit = suspendTransaction {
        Employees.deleteWhere { Employees.id eq id }
    }


}