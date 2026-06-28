package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DatabaseDao {
    // Connections
    @Query("SELECT * FROM connections ORDER BY createdAt DESC")
    fun getAllConnections(): Flow<List<ConnectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: ConnectionEntity)

    @Query("DELETE FROM connections WHERE id = :id")
    suspend fun deleteConnection(id: Int)

    // Query History
    @Query("SELECT * FROM query_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentQueries(): Flow<List<QueryHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueryHistory(queryHistory: QueryHistoryEntity)
}
