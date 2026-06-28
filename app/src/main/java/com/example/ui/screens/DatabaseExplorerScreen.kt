package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBlueBackground
import com.example.ui.theme.GrayText
import com.example.ui.theme.LightText
import com.example.ui.viewmodels.SqlEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseExplorerScreen(navController: NavController, viewModel: SqlEditorViewModel) {
    val schema by viewModel.schema.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Database Explorer", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ExplorerHeader("Tables")
            }
            
            items(schema.keys.toList()) { tableName ->
                var expanded by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.TableChart,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(tableName, color = LightText, fontWeight = FontWeight.Medium)
                }
                
                if (expanded) {
                    val columns = schema[tableName] ?: emptyList()
                    columns.forEach { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 64.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewList,
                                contentDescription = null,
                                tint = GrayText,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 8.dp)
                            )
                            Text(col, color = GrayText, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExplorerHeader(title: String) {
    Text(
        text = title,
        color = GrayText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}
