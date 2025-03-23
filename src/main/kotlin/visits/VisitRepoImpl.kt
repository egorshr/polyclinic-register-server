package com.example.visits

import com.example.tables.Visits
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class VisitRepoImpl : VisitRepo {

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
        newSuspendedTransaction(Dispatchers.IO, statement = block)

    override suspend fun getAllVisits(): List<Visit> = suspendTransaction {
        Visits.selectAll().map { row ->
            Visit(
                id = row[Visits.id],
                discountId = row[Visits.discountId],
                patientId = row[Visits.patientId],
                employeeId = row[Visits.employeeId],
                dateAndTime = row[Visits.visitDateAndTime]
            )
        }
    }



    override suspend fun updateVisit(visit: Visit): Unit = suspendTransaction {
        Visits.update({ Visits.id eq visit.id }) {
            it[Visits.discountId] = visit.discountId
            it[Visits.patientId] = visit.patientId
            it[Visits.employeeId] = visit.employeeId
            it[Visits.visitDateAndTime] = visit.dateAndTime
        }
    }

    override suspend fun deleteVisit(id: Int): Unit = suspendTransaction {
        Visits.deleteWhere { Visits.id eq id }
    }

}