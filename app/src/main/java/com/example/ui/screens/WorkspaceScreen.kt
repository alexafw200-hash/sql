package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.ConnectionEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(navController: NavController, viewModel: com.example.ui.viewmodels.WorkspaceViewModel) {
    val connections by viewModel.connections.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = buildAnnotatedString {
                                append("SQL ")
                                withStyle(style = SpanStyle(color = CyanPrimary)) {
                                    append("RUN")
                                }
                            },
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBlueSurface.copy(alpha = 0.9f),
                        titleContentColor = LightText
                    )
                )
                HorizontalDivider(color = BorderLight, thickness = 1.dp)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.SqlEditor.route) },
                containerColor = CyanPrimary,
                contentColor = DarkBlueBackground
            ) {
                Icon(Icons.Default.Code, contentDescription = "New Query")
            }
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                WorkspaceSectionTitle("Tools")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ToolCard(
                        title = "SQL Editor",
                        icon = Icons.Default.Code,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                    ToolCard(
                        title = "Translator",
                        icon = Icons.Default.Translate,
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate(Screen.SqlTranslator.route) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WorkspaceSectionTitle("Connections")
                    TextButton(onClick = { navController.navigate(Screen.ConnectionManager.route) }) {
                        Text("+ New", color = CyanPrimary)
                    }
                }
            }

            if (connections.isEmpty()) {
                item {
                    Text(
                        "No connections found. Try adding a Local SQLite database.",
                        color = GrayText,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(connections) { connection ->
                    ConnectionItem(
                        connection = connection,
                        onClick = {
                            // In a full app, we would pass connection ID to editor
                            navController.navigate(Screen.SqlEditor.route)
                        }
                    )
                }
            }
            
            // Dummy item for local DB to match design
            if (connections.isEmpty()) {
                 item {
                    ConnectionItem(
                        connection = ConnectionEntity(
                            name = "Local SQLite",
                            type = "SQLite"
                        ),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                 }
            }
            
            item {
                 Spacer(modifier = Modifier.height(80.dp)) // FAB padding
            }
        }
    }
}

@Composable
fun WorkspaceSectionTitle(title: String) {
    Text(
        text = title,
        color = LightText,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ToolCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = DarkBlueSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CyanPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = LightText, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ConnectionItem(connection: ConnectionEntity, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        color = DarkBlueSurfaceVariant.copy(alpha = 0.8f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkBlueBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = if (connection.type == "SQLite") Color(0xFF64B5F6) else CyanPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(connection.name, color = LightText, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                Text(connection.type, color = GrayText, fontSize = 12.sp)
            }
        }
    }
}
