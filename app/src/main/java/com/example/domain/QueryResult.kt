package com.example.domain

data class QueryResult(
    val columns: List<String>,
    val rows: List<List<String>>,
    val executionTimeMs: Long,
    val error: String? = null,
    val rowsAffected: Int = 0
)
