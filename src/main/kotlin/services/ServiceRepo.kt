package com.example.services

import kotlinx.coroutines.flow.Flow

interface ServiceRepo {
    suspend fun getAllServices(order: String = "asc"): List<Service>
    suspend fun createService(service: Service): Service
    suspend fun updateService(service: Service)
    suspend fun deleteService(id: Int)
}