package com.example.noted.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home")
    object EditNoteScreen: Screen("edit_note/{id}")
    object NewNoteScreen: Screen("new_note")
}
