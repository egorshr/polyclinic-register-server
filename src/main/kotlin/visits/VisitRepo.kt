package com.example.visits

import kotlinx.datetime.Instant

interface VisitRepo {
    suspend fun getAllVisits(startDate: Instant? = null, endDate: Instant? = null): List<Visit>
    suspend fun updateVisit(visit: Visit)
    suspend fun deleteVisit(id: Int)
}