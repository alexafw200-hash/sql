package com.example.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.system.measureTimeMillis

class H2DatabaseEngine(private val mode: String = "MySQL") : DatabaseEngine {
    private var connection: Connection? = null

    override suspend fun connect(connectionDetails: Any): Boolean = withContext(Dispatchers.IO) {
        try {
            // Load H2 driver explicitly
            Class.forName("org.h2.Driver")
            
            val dbName = connectionDetails as? String ?: "local_workspace"
            val url = "jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1;MODE=$mode;DATABASE_TO_UPPER=false"
            connection = DriverManager.getConnection(url, "sa", "")
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun executeQuery(query: String): QueryResult = withContext(Dispatchers.IO) {
        val conn = connection ?: return@withContext QueryResult(emptyList(), emptyList(), 0, "Database not connected")
        var error: String? = null
        val columns = mutableListOf<String>()
        val rows = mutableListOf<List<String>>()
        var rowsAffected = 0

        val timeMs = measureTimeMillis {
            try {
                conn.createStatement().use { statement ->
                    val isResultSet = statement.execute(query)
                    if (isResultSet) {
                        statement.resultSet.use { rs ->
                            val metaData = rs.metaData
                            val columnCount = metaData.columnCount
                            for (i in 1..columnCount) {
                                columns.add(metaData.getColumnName(i))
                            }
                            
                            while (rs.next()) {
                                val row = mutableListOf<String>()
                                for (i in 1..columnCount) {
                                    row.add(rs.getString(i) ?: "NULL")
                                }
                                rows.add(row)
                            }
                        }
                    } else {
                        rowsAffected = statement.updateCount
                    }
                }
            } catch (e: Exception) {
                error = e.message
            }
        }
        QueryResult(columns, rows, timeMs, error, rowsAffected)
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        connection?.close()
        connection = null
    }

    override suspend fun getTables(): List<String> = withContext(Dispatchers.IO) {
        val tables = mutableListOf<String>()
        try {
            val conn = connection ?: return@withContext emptyList()
            val metaData = conn.metaData
            metaData.getTables(null, "PUBLIC", null, arrayOf("TABLE", "VIEW")).use { rs ->
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tables
    }

    override suspend fun getTableSchema(tableName: String): List<String> = withContext(Dispatchers.IO) {
        val schema = mutableListOf<String>()
        try {
            val conn = connection ?: return@withContext emptyList()
            val metaData = conn.metaData
            metaData.getColumns(null, "PUBLIC", tableName, null).use { rs ->
                while (rs.next()) {
                    val colName = rs.getString("COLUMN_NAME")
                    val typeName = rs.getString("TYPE_NAME")
                    schema.add("$colName ($typeName)")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        schema
    }
}
