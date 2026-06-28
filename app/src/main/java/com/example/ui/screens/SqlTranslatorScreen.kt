package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.domain.SqlTranslationEngine
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DarkBlueBackground
import com.example.ui.theme.DarkBlueSurface
import com.example.ui.theme.GrayText
import com.example.ui.theme.LightText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlTranslatorScreen(navController: NavController) {
    var sourceQuery by remember { mutableStateOf("") }
    var translatedQuery by remember { mutableStateOf("CREATE TABLE users (\n  id INTEGER AUTO_INCREMENT\n    PRIMARY KEY,\n  name VARCHAR(255),\n  email VARCHAR(255)\n);") }
    
    var fromEngine by remember { mutableStateOf("SQLite") }
    var toEngine by remember { mutableStateOf("MySQL") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SQL Translator", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
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
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("From", color = GrayText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                    EngineSelector(selected = fromEngine, onSelect = { fromEngine = it })
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("To", color = GrayText, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                    EngineSelector(selected = toEngine, onSelect = { toEngine = it })
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = sourceQuery,
                onValueChange = { sourceQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(color = LightText, fontFamily = FontFamily.Monospace),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkBlueSurface,
                    unfocusedContainerColor = DarkBlueSurface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = CyanPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = translatedQuery,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(color = Color(0xFFE6EE9C), fontFamily = FontFamily.Monospace), // Match yellowish text in picture
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkBlueSurface,
                    unfocusedContainerColor = DarkBlueSurface,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = CyanPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (sourceQuery.isNotEmpty()) {
                        translatedQuery = SqlTranslationEngine.translate(sourceQuery, fromEngine, toEngine)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Translate", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EngineSelector(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val engines = listOf("SQLite", "MySQL", "PostgreSQL", "MariaDB", "SQL Server")
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkBlueSurface,
                unfocusedContainerColor = DarkBlueSurface,
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = LightText,
                unfocusedTextColor = LightText
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkBlueSurface)
        ) {
            engines.forEach { engine ->
                DropdownMenuItem(
                    text = { Text(engine, color = LightText) },
                    onClick = {
                        onSelect(engine)
                        expanded = false
                    }
                )
            }
        }
    }
}
