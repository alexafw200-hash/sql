package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.DatabaseRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "sqlrun_database"
        ).build()
        repository = DatabaseRepository(database.databaseDao())

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val factory = ViewModelFactory(application, repository)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Workspace.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Workspace.route) {
                            WorkspaceScreen(
                                navController = navController,
                                viewModel = viewModel(factory = factory)
                            )
                        }
                        composable(Screen.SqlEditor.route) {
                            SqlEditorScreen(
                                navController = navController,
                                viewModel = viewModel(factory = factory)
                            )
                        }
                        composable(Screen.ConnectionManager.route) {
                            ConnectionManagerScreen(
                                navController = navController,
                                viewModel = viewModel(factory = factory)
                            )
                        }
                        composable(Screen.DatabaseExplorer.route) {
                            DatabaseExplorerScreen(
                                navController = navController,
                                viewModel = viewModel(factory = factory)
                            )
                        }
                        composable(Screen.SqlTranslator.route) {
                            SqlTranslatorScreen(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
