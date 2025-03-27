package com.example.services

import kotlinx.coroutines.flow.Flow

interface ServiceRepo {
    suspend fun getAllServices(): List<Service>
    suspend fun updateService(service: Service)
    suspend fun deleteService(id: Int)
}