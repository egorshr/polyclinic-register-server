package com.example.services

import com.example.tables.Services
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ServiceRepoImpl : ServiceRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun getAllServices(order: String): List<Service> = suspendTransaction {
        val sortOrder = when (order.lowercase()) {
            "desc" -> SortOrder.DESC
            else -> SortOrder.ASC
        }
        Services.selectAll()
            .orderBy(Services.servicePrice to sortOrder)
            .map {
                Service(
                    id = it[Services.id],
                    name = it[Services.serviceName],
                    price = it[Services.servicePrice].toDouble()
                )
            }
    }

    override suspend fun createService(service: Service): Service = suspendTransaction {
        val id = Services.insert {
            it[Services.serviceName] = service.name
            it[Services.servicePrice] = service.price.toBigDecimal()
        }[Services.id]

        service.copy(id = id)
    }

    override suspend fun updateService(service: Service): Unit = suspendTransaction {
        try {
            Services.update({ Services.id eq service.id }) {
                it[Services.serviceName] = service.name
                it[Services.servicePrice] = service.price.toBigDecimal()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteService(id: Int): Unit = suspendTransaction {
        try {
            Services.deleteWhere { Services.id eq id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}