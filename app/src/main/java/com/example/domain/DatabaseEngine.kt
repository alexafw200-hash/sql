package com.example.domain

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.measureTimeMillis

interface DatabaseEngine {
    suspend fun connect(connectionDetails: Any): Boolean
    suspend fun executeQuery(query: String): QueryResult
    suspend fun disconnect()
    suspend fun getTables(): List<String>
    suspend fun getTableSchema(tableName: String): List<String>
}

class LocalSqliteEngine(private val context: Context) : DatabaseEngine {
    private var db: SQLiteDatabase? = null

    override suspend fun connect(connectionDetails: Any): Boolean = withContext(Dispatchers.IO) {
        try {
            val dbName = connectionDetails as? String ?: "local_workspace.db"
            val dbFile = context.getDatabasePath(dbName)
            if (!dbFile.parentFile?.exists()!!) {
                dbFile.parentFile?.mkdirs()
            }
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun executeQuery(query: String): QueryResult = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext QueryResult(emptyList(), emptyList(), 0, "Database not connected")
        var error: String? = null
        val columns = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()
        var rowsAffected = 0

        val timeMs = measureTimeMillis {
            try {
                if (query.trim().uppercase().startsWith("SELECT") || query.trim().uppercase().startsWith("PRAGMA")) {
                    database.rawQuery(query, null).use { cursor ->
                        columns.addAll(cursor.columnNames)
                        while (cursor.moveToNext()) {
                            val row = mutableListOf<String>()
                            for (i in 0 until cursor.columnCount) {
                                row.add(cursor.getString(i) ?: "NULL")
                            }
                            rows.add(row)
                        }
                    }
                } else {
                    database.execSQL(query)
                    rowsAffected = 1 // Simplified, actual rows affected is harder with raw execSQL in basic Android SQLite unless using compileStatement
                }
            } catch (e: Exception) {
                error = e.message
            }
        }
        QueryResult(columns, rows, timeMs, error, rowsAffected)
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        db?.close()
        db = null
    }

    override suspend fun getTables(): List<String> {
        val result = executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'")
        return result.rows.map { it.first() }
    }
    
    override suspend fun getTableSchema(tableName: String): List<String> {
        val result = executeQuery("PRAGMA table_info($tableName)")
        // PRAGMA table_info returns: cid, name, type, notnull, dflt_value, pk
        return result.rows.map { "${it[1]} (${it[2]})" }
    }
}
