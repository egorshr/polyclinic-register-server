package com.example.employees

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int,
    val specialityId: Int,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val birthday: LocalDate,
    val gender: Char,
    val jobTitle: String,
    val phoneNumber: String,
    val durationOfVisit: LocalTime?,
    val username: String,
    val email: String,
    val password: String
)