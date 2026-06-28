package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ConnectionEntity
import com.example.data.DatabaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkspaceViewModel(private val repository: DatabaseRepository) : ViewModel() {
    
    val connections: StateFlow<List<ConnectionEntity>> = repository.allConnections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
    fun addConnection(connection: ConnectionEntity) {
        viewModelScope.launch {
            repository.insertConnection(connection)
        }
    }
    
    fun deleteConnection(id: Int) {
        viewModelScope.launch {
            repository.deleteConnection(id)
        }
    }
}
