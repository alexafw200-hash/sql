package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connections")
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // SQLite, MySQL, PostgreSQL, MariaDB
    val host: String = "",
    val port: String = "",
    val username: String = "",
    val passwordEncrypted: String = "",
    val databaseName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
