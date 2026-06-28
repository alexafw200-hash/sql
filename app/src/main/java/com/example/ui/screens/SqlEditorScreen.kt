package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
    var queryText by remember { mutableStateOf("SELECT * FROM users;\n") }
    val queryResult by viewModel.queryResult.collectAsStateWithLifecycle()
    val isExecuting by viewModel.isExecuting.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("SQL Editor", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.DatabaseExplorer.route) }) {
                            Icon(Icons.Default.TableChart, contentDescription = "Database Explorer", tint = GrayText)
                        }
                        IconButton(
                            onClick = { viewModel.executeQuery(queryText) },
                            enabled = !isExecuting
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Execute",
                                tint = if (isExecuting) GrayText else CyanPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBlueSurface.copy(alpha = 0.9f),
                        titleContentColor = LightText,
                        navigationIconContentColor = LightText
                    )
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
            }
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Editor Area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = DarkBlueSurface
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    BasicTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        textStyle = TextStyle(
                            color = CyanPrimary, // Simple syntax coloring placeholder
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(CyanPrimary),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Results Area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = DarkBlueSurfaceVariant,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = 0,
                        containerColor = DarkBlueSurfaceVariant,
                        contentColor = CyanPrimary
                    ) {
                        Tab(
                            selected = true,
                            onClick = { },
                            text = { Text("RESULT PREVIEW", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp) }
                        )
                        Tab(
                            selected = false,
                            onClick = { },
                            text = { Text("MESSAGES", color = GrayText) }
                        )
                    }
                    
                    if (isExecuting) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = CyanPrimary)
                        }
                    } else {
                        queryResult?.let { result ->
                            ResultGrid(result = result)
                        } ?: run {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No results to display. Run a query.", color = GrayText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultGrid(result: QueryResult) {
    if (result.error != null) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(text = "Error: ${result.error}", color = ErrorRed)
        }
        return
    }

    if (result.columns.isEmpty() && result.rowsAffected >= 0) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(text = "Success. Rows affected: ${result.rowsAffected}\nTime: ${result.executionTimeMs}ms", color = LightText)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "${result.rows.size} rows in set (${result.executionTimeMs / 1000f} sec)",
            color = GrayText,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            Column {
                // Header
                Row(modifier = Modifier.background(DarkBlueSurface)) {
                    result.columns.forEach { col ->
                        Text(
                            text = col,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                                .widthIn(min = 100.dp)
                        )
                    }
                }
                
                // Rows
                LazyColumn {
                    items(result.rows) { row ->
                        Row {
                            row.forEach { cell ->
                                Text(
                                    text = cell,
                                    color = LightText,
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .widthIn(min = 100.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = DarkBlueSurface, thickness = 1.dp)
                    }
                }
            }
        }
    }
}
