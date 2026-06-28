package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DatabaseRepository
import com.example.domain.DatabaseEngine
import com.example.domain.LocalSqliteEngine
import com.example.domain.QueryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SqlEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val engine: DatabaseEngine = LocalSqliteEngine(application)
    
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
            engine.connect("local_workspace.db")
            refreshSchema()
            
            // Create some default tables if empty
            val result = engine.executeQuery("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, status TEXT)")
            engine.executeQuery("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, amount REAL, created_at TEXT)")
            
            // Insert some dummy data for preview
            engine.executeQuery("INSERT OR IGNORE INTO users (id, name, email, status) VALUES (1, 'Ali', 'ali@example.com', 'active')")
            engine.executeQuery("INSERT OR IGNORE INTO users (id, name, email, status) VALUES (2, 'Omar', 'omar@example.com', 'active')")
            engine.executeQuery("INSERT OR IGNORE INTO users (id, name, email, status) VALUES (3, 'Sara', 'sara@example.com', 'active')")
            
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

    fun executeQuery(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isExecuting.value = true
            _queryResult.value = engine.executeQuery(query)
            if (query.trim().uppercase().startsWith("CREATE") || query.trim().uppercase().startsWith("DROP") || query.trim().uppercase().startsWith("ALTER")) {
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
