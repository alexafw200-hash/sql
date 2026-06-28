package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.ConnectionEntity
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBlueBackground
import com.example.ui.theme.DarkBlueSurface
import com.example.ui.theme.LightText
import com.example.ui.viewmodels.WorkspaceViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionManagerScreen(navController: NavController, viewModel: WorkspaceViewModel) {
    var connectionName by remember { mutableStateOf("MySQL Server") }
    var host by remember { mutableStateOf("localhost") }
    var port by remember { mutableStateOf("3306") }
    var username by remember { mutableStateOf("root") }
    var password by remember { mutableStateOf("root") }
    var databaseName by remember { mutableStateOf("testdb") }
    var connectionType by remember { mutableStateOf("MySQL") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Connection", fontSize = 18.sp) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkBlueBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ConnectionField(label = "Connection Name", value = connectionName, onValueChange = { connectionName = it })
            ConnectionField(label = "Host", value = host, onValueChange = { host = it })
            ConnectionField(label = "Port", value = port, onValueChange = { port = it })
            ConnectionField(label = "Username", value = username, onValueChange = { username = it })
            ConnectionField(label = "Password", value = password, onValueChange = { password = it }, isPassword = true)
            ConnectionField(label = "Database Name", value = databaseName, onValueChange = { databaseName = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val newConn = ConnectionEntity(
                            name = connectionName,
                            type = connectionType,
                            host = host,
                            port = port,
                            username = username,
                            passwordEncrypted = password, // basic mock
                            databaseName = databaseName
                        )
                        viewModel.addConnection(newConn)
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text("Save", color = DarkBlueBackground, fontWeight = FontWeight.Bold)
                }
                
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Connection tested successfully! (Simulated)")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary)
                ) {
                    Text("Test Connection")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = LightText) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = DarkBlueSurface,
            unfocusedContainerColor = DarkBlueSurface,
            focusedBorderColor = CyanPrimary,
            unfocusedBorderColor = DarkBlueSurface,
            focusedTextColor = LightText,
            unfocusedTextColor = LightText
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}
