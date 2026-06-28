package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.QueryResult
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodels.SqlEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlEditorScreen(navController: NavController, viewModel: SqlEditorViewModel) {
    var queryText by remember { mutableStateOf("SELECT u.id, u.name, u.email\nFROM users u\nINNER JOIN orders o ON u.id = o.user_id\nWHERE u.status = 'active'\nORDER BY o.created_at DESC\nLIMIT 100;") }
    val queryResult by viewModel.queryResult.collectAsStateWithLifecycle()
    val isExecuting by viewModel.isExecuting.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SQL RUN", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlueBackground,
                    titleContentColor = LightText,
                    navigationIconContentColor = LightText
                )
            )
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Toolbar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var expanded by remember { mutableStateOf(false) }
                val databases = listOf("Local SQLite", "MySQL Server", "PostgreSQL DB", "MariaDB Test", "SQL Server")
                var selectedDb by remember { mutableStateOf(databases[0]) }

                // Database selector
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = DarkBlueSurfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = LightText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(selectedDb, color = LightText, fontSize = 14.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LightText, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(DarkBlueSurface)
                    ) {
                        databases.forEach { db ->
                            DropdownMenuItem(
                                text = { Text(db, color = LightText) },
                                onClick = {
                                    selectedDb = db
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(
                    onClick = { 
                        val engineKey = when (selectedDb) {
                            "Local SQLite" -> "SQLite"
                            "MySQL Server" -> "MySQL"
                            "PostgreSQL DB" -> "PostgreSQL"
                            "MariaDB Test" -> "MariaDB"
                            "SQL Server" -> "SQL Server"
                            else -> "SQLite"
                        }
                        viewModel.executeQuery(queryText, engineKey) 
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1976D2).copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = CyanPrimary)
                }
                
                IconButton(onClick = { navController.navigate(Screen.DatabaseExplorer.route) }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = LightText)
                }
            }

            // Editor Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val lines = queryText.split("\n")
                Row {
                    // Line numbers
                    Column(
                        modifier = Modifier.padding(end = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lines.size) {
                            Text(
                                text = i.toString(),
                                color = GrayText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                        }
                    }
                    // Editor
                    BasicTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        textStyle = TextStyle(
                            color = LightText, 
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(CyanPrimary),
                        visualTransformation = SqlVisualTransformation(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Results Area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f), // Make it a bit taller based on picture
                color = DarkBlueSurface,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkBlueSurface,
                        contentColor = CyanPrimary,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = CyanPrimary
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("RESULTS", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("MESSAGES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GrayText) }
                        )
                    }

                    val clipboardManager = LocalClipboardManager.current
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = CyanPrimary
                            )
                        } else {
                            if (selectedTab == 0) {
                                // RESULTS TAB
                                if (queryResult?.error != null) {
                                    Text(
                                        "Error executing query. See MESSAGES tab.",
                                        color = ErrorRed,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else if (queryResult != null) {
                                    val result = queryResult!!
                                    if (result.rows.isEmpty()) {
                                        Text(
                                            "No rows returned.",
                                            color = GrayText,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    } else {
                                        ResultsTable(columns = result.columns, data = result.rows)
                                    }
                                } else {
                                    // Default table
                                    val defaultCols = listOf("id", "name", "email", "status")
                                    val defaultData = listOf(
                                        listOf("1", "Ali", "ali@example.com", "active"),
                                        listOf("2", "Omar", "omar@example.com", "active"),
                                        listOf("3", "Sara", "sara@example.com", "active"),
                                        listOf("4", "Ahmed", "ahmed@example.com", "active"),
                                        listOf("5", "Lina", "lina@example.com", "active")
                                    )
                                    ResultsTable(columns = defaultCols, data = defaultData)
                                }
                            } else {
                                // MESSAGES TAB
                                if (queryResult?.error != null) {
                                    Text(
                                        queryResult!!.error!!,
                                        color = ErrorRed,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else if (queryResult != null) {
                                    Text(
                                        "Query executed successfully in ${queryResult!!.executionTimeMs}ms.\nRows affected: ${queryResult!!.rowsAffected}",
                                        color = LightText,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    Text(
                                        "No messages.",
                                        color = GrayText,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Bottom status bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            val rowCount = queryResult?.rows?.size ?: if (queryResult != null) 0 else 5
                            Text("$rowCount rows", color = GrayText, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            val timeSec = queryResult?.executionTimeMs?.let { it / 1000.0 } ?: 0.032
                            Text("${timeSec}s", color = GrayText, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val textToCopy = if (selectedTab == 0) {
                                        if (queryResult?.error == null && queryResult?.rows?.isNotEmpty() == true) {
                                            val header = queryResult!!.columns.joinToString("\t")
                                            val data = queryResult!!.rows.joinToString("\n") { it.joinToString("\t") }
                                            "$header\n$data"
                                        } else ""
                                    } else {
                                        if (queryResult?.error != null) {
                                            queryResult!!.error!!
                                        } else if (queryResult != null) {
                                            "Query executed successfully in ${queryResult!!.executionTimeMs}ms.\nRows affected: ${queryResult!!.rowsAffected}"
                                        } else ""
                                    }
                                    if (textToCopy.isNotEmpty()) {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(textToCopy))
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GrayText, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(
                                onClick = { /* Export */ },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = GrayText, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultsTable(columns: List<String>, data: List<List<String>>) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            columns.forEachIndexed { index, col ->
                Text(
                    text = col,
                    color = LightText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(if (index == 0) 50.dp else if (index == 2) 180.dp else 100.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }
        HorizontalDivider(color = BorderLight, thickness = 1.dp)
        
        // Data Rows
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(data) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    row.forEachIndexed { index, cell ->
                        Text(
                            text = cell,
                            color = LightText,
                            modifier = Modifier
                                .width(if (index == 0) 50.dp else if (index == 2) 180.dp else 100.dp)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
            }
        }
    }
}

class SqlVisualTransformation : androidx.compose.ui.text.input.VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "INNER", "JOIN", "ON", "ORDER", "BY", 
            "LIMIT", "CREATE", "TABLE", "INSERT", "INTO", "VALUES", "UPDATE", 
            "SET", "DELETE", "AND", "OR", "NOT", "AS", "PRIMARY", "KEY", 
            "AUTOINCREMENT", "AUTO_INCREMENT", "INT", "INTEGER", "VARCHAR", 
            "TEXT", "DEFAULT", "TIMESTAMP", "CURRENT_TIMESTAMP", "UNIQUE", 
            "NOT NULL", "NULL", "IF", "EXISTS", "DROP", "ALTER", "ADD", "MODIFY", "COLUMN"
        )
        
        val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
            val str = text.text
            val wordRegex = "\\b\\w+\\b".toRegex()
            var lastIndex = 0
            
            for (match in wordRegex.findAll(str)) {
                append(str.substring(lastIndex, match.range.first))
                val word = match.value
                if (keywords.contains(word.uppercase())) {
                    withStyle(style = androidx.compose.ui.text.SpanStyle(color = CyanPrimary)) {
                        append(word)
                    }
                } else {
                    append(word)
                }
                lastIndex = match.range.last + 1
            }
            append(str.substring(lastIndex))
        }
        return androidx.compose.ui.text.input.TransformedText(annotatedString, androidx.compose.ui.text.input.OffsetMapping.Identity)
    }
}
