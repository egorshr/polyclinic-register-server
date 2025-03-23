package com.example.services

interface ServiceRepo {
    suspend fun getAllServices(): List<Service>
    suspend fun updateService(service: Service)
    suspend fun deleteService(id: Int)
}