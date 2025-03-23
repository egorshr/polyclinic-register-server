package com.example.visits

interface VisitRepo {
    suspend fun getAllVisits(): List<Visit>
    suspend fun updateVisit(visit: Visit)
    suspend fun deleteVisit(id: Int)
}