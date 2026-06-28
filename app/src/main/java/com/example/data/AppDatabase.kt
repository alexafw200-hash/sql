package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ConnectionEntity::class, QueryHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao
}
