package com.example.visits

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@Serializable
data class Visit(
    val id: Int,
    val discountId: Int,
    val patientId: Int,
    val employeeId: Int,
    val dateAndTime: Instant
)