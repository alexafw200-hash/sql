package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "query_history")
data class QueryHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val connectionId: Int, // Can be -1 for general workspace queries
    val queryText: String,
    val timestamp: Long = System.currentTimeMillis()
)
