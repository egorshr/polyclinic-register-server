package com.example.employees

import kotlinx.coroutines.flow.Flow

interface EmployeeRepo {
    suspend fun getAllEmployees(): List<Employee>
    suspend fun updateEmployee(employee: Employee)
    suspend fun deleteEmployee(id: Int)
}