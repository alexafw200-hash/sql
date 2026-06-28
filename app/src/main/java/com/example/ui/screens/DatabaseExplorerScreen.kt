package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBlueBackground
import com.example.ui.theme.DarkBlueSurfaceVariant
import com.example.ui.theme.GrayText
import com.example.ui.theme.LightText
import com.example.ui.viewmodels.SqlEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseExplorerScreen(navController: NavController, viewModel: SqlEditorViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database Explorer", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = LightText)
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
            // Database selector
            Surface(
                color = DarkBlueSurfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = LightText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Local SQLite", color = LightText, fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = LightText, modifier = Modifier.size(20.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    ExplorerCategory(
                        title = "Tables", 
                        schema = mapOf(
                            "users" to listOf(
                                Pair("id", "INTEGER"),
                                Pair("name", "TEXT"),
                                Pair("email", "TEXT"),
                                Pair("status", "TEXT"),
                                Pair("created_at", "TEXT")
                            ),
                            "orders" to listOf(
                                Pair("id", "INTEGER"),
                                Pair("user_id", "INTEGER"),
                                Pair("amount", "REAL"),
                                Pair("created_at", "TEXT")
                            )
                        ), 
                        isExpandedByDefault = true,
                        isTableExpandedByDefault = true
                    )
                }
                
                item {
                    ExplorerCategory(
                        title = "Views",
                        schema = mapOf("user_orders" to emptyList()), 
                        isExpandedByDefault = true
                    )
                }

                item {
                    ExplorerCategory(
                        title = "Indexes",
                        schema = mapOf("idx_orders_user_id" to emptyList()), 
                        isExpandedByDefault = true
                    )
                }
            }
        }
    }
}

@Composable
fun ExplorerCategory(
    title: String, 
    schema: Map<String, List<Pair<String, String>>>, 
    isExpandedByDefault: Boolean = false,
    isTableExpandedByDefault: Boolean = false
) {
    var expanded by remember { mutableStateOf(isExpandedByDefault) }
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = LightText,
                modifier = Modifier.size(20.dp).padding(end = 8.dp)
            )
            Text(title, color = LightText, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
        
        if (expanded) {
            schema.forEach { (tableName, columns) ->
                var tableExpanded by remember { mutableStateOf(isTableExpandedByDefault && tableName == "users") }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { tableExpanded = !tableExpanded }
                        .padding(start = 40.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (tableExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.size(16.dp).padding(end = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(16.dp).padding(end = 8.dp)
                    )
                    Text(tableName, color = LightText, fontSize = 14.sp)
                }
                
                if (tableExpanded) {
                    columns.forEach { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 80.dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ViewList,
                                contentDescription = null,
                                tint = GrayText,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(col.first, color = LightText, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("(${col.second})", color = GrayText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
