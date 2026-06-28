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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceScreen(navController: NavController, viewModel: com.example.ui.viewmodels.WorkspaceViewModel) {
    val connections by viewModel.connections.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkBlueSurface
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "SQL RUN",
                    modifier = Modifier.padding(16.dp),
                    color = CyanPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(color = BorderLight)
                
                NavigationDrawerItem(
                    label = { Text("SQL Editor", color = LightText) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.SqlEditor.route) 
                    },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, tint = CyanPrimary) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                
                NavigationDrawerItem(
                    label = { Text("Database Explorer", color = LightText) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.DatabaseExplorer.route) 
                    },
                    icon = { Icon(Icons.Default.Storage, contentDescription = null, tint = CyanPrimary) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                
                NavigationDrawerItem(
                    label = { Text("SQL Translator", color = LightText) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.SqlTranslator.route) 
                    },
                    icon = { Icon(Icons.Default.Translate, contentDescription = null, tint = CyanPrimary) },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text("Projects", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                    actions = {
                        IconButton(onClick = { /* Search */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = LightText)
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.ConnectionManager.route) },
                containerColor = Color(0xFF1976D2), // Match the blue button in image
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Connection") },
                text = { Text("New Connection", fontWeight = FontWeight.SemiBold) },
                shape = RoundedCornerShape(12.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = DarkBlueBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search projects", color = GrayText) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GrayText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkBlueSurface,
                        unfocusedContainerColor = DarkBlueSurface,
                        focusedBorderColor = DarkBlueSurface,
                        unfocusedBorderColor = DarkBlueSurface,
                        focusedTextColor = LightText,
                        unfocusedTextColor = LightText,
                        cursorColor = CyanPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
            }

            if (connections.isEmpty()) {
                 item {
                    ConnectionItem(
                        connection = ConnectionEntity(name = "Local SQLite", type = "SQLite"),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                    ConnectionItem(
                        connection = ConnectionEntity(name = "MySQL Server", type = "MySQL"),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                    ConnectionItem(
                        connection = ConnectionEntity(name = "PostgreSQL DB", type = "PostgreSQL"),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                    ConnectionItem(
                        connection = ConnectionEntity(name = "MariaDB Test", type = "MariaDB"),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                    ConnectionItem(
                        connection = ConnectionEntity(name = "SQL Server", type = "SQL Server"),
                        onClick = { navController.navigate(Screen.SqlEditor.route) }
                    )
                 }
            } else {
                items(connections.filter { it.name.contains(searchQuery, ignoreCase = true) }) { connection ->
                    ConnectionItem(
                        connection = connection,
                        onClick = {
                            navController.navigate(Screen.SqlEditor.route)
                        }
                    )
                }
            }
            
            item {
                 Spacer(modifier = Modifier.height(80.dp)) // FAB padding
            }
        }
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
        color = DarkBlueSurfaceVariant.copy(alpha = 0.3f), // More subtle background
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                // Determine icon color based on type to match the picture
                val iconTint = when (connection.type) {
                    "SQLite" -> Color(0xFF64B5F6) // Light blue
                    "MySQL" -> Color(0xFFF57C00) // Orange
                    "PostgreSQL" -> Color(0xFF1976D2) // Blue
                    "MariaDB" -> Color(0xFF607D8B) // Slate
                    "SQL Server" -> Color(0xFFD32F2F) // Red
                    else -> CyanPrimary
                }
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
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
