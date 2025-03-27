package com.example.services

import com.example.tables.Services
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class ServiceRepoImpl : ServiceRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)


    override suspend fun getAllServices(): List<Service> = suspendTransaction {
          Services.selectAll().map {
            Service(
                id = it[Services.id],
                name = it[Services.serviceName],
                price = it[Services.servicePrice].toDouble()
            )
        }

    }

    override suspend fun updateService(service: Service): Unit = suspendTransaction {
        Services.update({ Services.id eq service.id }) {
            it[Services.serviceName] = service.name
            it[Services.servicePrice] = service.price.toBigDecimal()
        }
    }

    override suspend fun deleteService(id: Int): Unit = suspendTransaction{
        Services.deleteWhere { Services.id eq id }
    }

}