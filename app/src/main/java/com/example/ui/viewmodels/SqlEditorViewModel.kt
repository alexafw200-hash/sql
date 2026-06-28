package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DatabaseRepository
import com.example.domain.DatabaseEngine
import com.example.domain.H2DatabaseEngine
import com.example.domain.LocalSqliteEngine
import com.example.domain.QueryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SqlEditorViewModel(application: Application) : AndroidViewModel(application) {

    private var currentEngineType: String = "SQLite"
    private var engine: DatabaseEngine = LocalSqliteEngine(application)
    private val app = application
    
    private val _queryResult = MutableStateFlow<QueryResult?>(null)
    val queryResult: StateFlow<QueryResult?> = _queryResult

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting
    
    private val _tables = MutableStateFlow<List<String>>(emptyList())
    val tables: StateFlow<List<String>> = _tables

    private val _schema = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val schema: StateFlow<Map<String, List<String>>> = _schema

    init {
        viewModelScope.launch {
            setupEngine("SQLite")
        }
    }
    
    private suspend fun setupEngine(engineType: String) {
        if (currentEngineType == engineType && _tables.value.isNotEmpty()) return
        
        engine.disconnect()
        
        val mode = when (engineType) {
            "MySQL", "MariaDB" -> "MySQL"
            "PostgreSQL" -> "PostgreSQL"
            "SQL Server" -> "MSSQLServer"
            else -> "MySQL" // default fallback for H2
        }
        
        engine = if (engineType == "SQLite") {
            LocalSqliteEngine(app)
        } else {
            H2DatabaseEngine(mode = mode)
        }
        
        currentEngineType = engineType
        
        val success = engine.connect("local_workspace_${mode}")
        if (success) {
            refreshSchema()
            
            // Create some default tables if empty to show something to the user
            if (engineType == "SQLite") {
                engine.executeQuery("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, status TEXT)")
                engine.executeQuery("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, amount REAL, created_at TEXT)")
                engine.executeQuery("INSERT OR IGNORE INTO users (id, name, email, status) VALUES (1, 'Ali', 'ali@example.com', 'active')")
                engine.executeQuery("INSERT OR IGNORE INTO users (id, name, email, status) VALUES (2, 'Omar', 'omar@example.com', 'active')")
            } else {
                val createUsersQuery = when (mode) {
                    "PostgreSQL" -> "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), status VARCHAR(50))"
                    "MSSQLServer" -> "CREATE TABLE IF NOT EXISTS users (id INT IDENTITY(1,1) PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), status VARCHAR(50))"
                    else -> "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), email VARCHAR(255), status VARCHAR(50))"
                }
                engine.executeQuery(createUsersQuery)
                
                // Avoid inserting duplicates on every reconnect to the same in-memory DB
                val checkUsers = engine.executeQuery("SELECT COUNT(*) AS c FROM users")
                if (checkUsers.rows.isEmpty() || checkUsers.rows[0][0] == "0") {
                    engine.executeQuery("INSERT INTO users (name, email, status) VALUES ('Ali', 'ali@example.com', 'active')")
                    engine.executeQuery("INSERT INTO users (name, email, status) VALUES ('Omar', 'omar@example.com', 'active')")
                }
            }
            
            refreshSchema()
        }
    }
    
    private suspend fun refreshSchema() {
        val tbls = engine.getTables()
        _tables.value = tbls
        
        val schemaMap = mutableMapOf<String, List<String>>()
        tbls.forEach { table ->
            schemaMap[table] = engine.getTableSchema(table)
        }
        _schema.value = schemaMap
    }

    fun executeQuery(query: String, engineType: String = "SQLite") {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isExecuting.value = true
            
            // Switch engine if needed before executing
            setupEngine(engineType)
            
            // We no longer need translation if we are using H2 natively! But for SQLite we might still want it if they paste MySQL.
            // For now, let's just pass the query directly to the engine, as H2 handles compatibility.
            // If they select SQLite but write MySQL, we can translate.
            val finalQuery = if (engineType == "SQLite") {
                com.example.domain.SqlTranslationEngine.translate(query, "MySQL", "SQLite")
            } else {
                query 
            }
            
            _queryResult.value = engine.executeQuery(finalQuery)
            if (finalQuery.trim().uppercase().startsWith("CREATE") || finalQuery.trim().uppercase().startsWith("DROP") || finalQuery.trim().uppercase().startsWith("ALTER") || finalQuery.trim().uppercase().startsWith("INSERT")) {
                refreshSchema()
            }
            _isExecuting.value = false
        }
    }
}

class ViewModelFactory(
    private val application: Application,
    private val repository: DatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkspaceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkspaceViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(SqlEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SqlEditorViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
