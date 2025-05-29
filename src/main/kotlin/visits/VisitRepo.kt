package com.example.visits

import kotlinx.datetime.Instant

interface VisitRepo {
    suspend fun getAllVisits(startDate: Instant? = null, endDate: Instant? = null): List<Visit>
    suspend fun getVisitsByPatientId(patientId: Int, startDate: Instant? = null, endDate: Instant? = null): List<Visit>
    suspend fun getVisitsByDoctorId(doctorId: Int, startDate: Instant? = null, endDate: Instant? = null): List<Visit>
    suspend fun createVisit(visit: Visit): Visit
    suspend fun updateVisit(visit: Visit)
    suspend fun deleteVisit(id: Set<Int>)
}