package com.example.visits

import com.example.employees.Employee
import com.example.employees.Specialty
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable


@Serializable
data class Visit(
    val id: Int,
    val discount: Discount,
    val patient: Patient,
    val employee: Employee,
    val dateAndTime: LocalDateTime
)

@Serializable
data class Discount(
    val id: Int,
    val percent: Int
)


@Serializable
data class Patient(
    val id: Int,
    val gender: Char,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val birthday: LocalDate,
    val phoneNumber: String,
    val passportSeries: String,
    val passportIssueDate: LocalDate,
    val passportIssuedBy: String,
    val addressCountry: String,
    val addressRegion: String,
    val addressLocality: String,
    val addressStreet: String,
    val addressHouse: Int,
    val addressBody: Int,
    val addressApartment: Int
)

