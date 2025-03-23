package com.example.employees

interface EmployeeRepo {
    suspend fun getAllEmployees(): List<Employee>
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(id: Int)
}