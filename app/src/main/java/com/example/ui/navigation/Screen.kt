package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Workspace : Screen("workspace")
    object ConnectionManager : Screen("connection_manager")
    object SqlEditor : Screen("sql_editor")
    object DatabaseExplorer : Screen("database_explorer")
    object SqlTranslator : Screen("sql_translator")
}
