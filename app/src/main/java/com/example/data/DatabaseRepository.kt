package com.example.data

import kotlinx.coroutines.flow.Flow

class DatabaseRepository(private val dao: DatabaseDao) {
    val allConnections: Flow<List<ConnectionEntity>> = dao.getAllConnections()
    val recentQueries: Flow<List<QueryHistoryEntity>> = dao.getRecentQueries()

    suspend fun insertConnection(connection: ConnectionEntity) = dao.insertConnection(connection)
    suspend fun deleteConnection(id: Int) = dao.deleteConnection(id)

    suspend fun insertQueryHistory(queryHistory: QueryHistoryEntity) = dao.insertQueryHistory(queryHistory)
}
