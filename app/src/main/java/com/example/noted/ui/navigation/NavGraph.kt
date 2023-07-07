package com.example.noted.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noted.data.NoteDao
import com.example.noted.data.NoteState
import com.example.noted.ui.EditNoteScreen
import com.example.noted.ui.HomeScreen
import com.example.noted.ui.NewNoteScreen
import com.example.noted.viewmodel.NoteEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun NavGraph(
    uiState: MutableStateFlow<NoteState>,
    notesMap: StateFlow<NoteState>,
    onEvent: (NoteEvent) -> Unit
) {

    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {

        composable(
            route = Screen.HomeScreen.route
        ) {
            HomeScreen(
                navController = navController,
                uiState = uiState,
                notesMap = notesMap,
                onEvent = onEvent
            )
        }

        composable(
            route = Screen.EditNoteScreen.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) {
            val id = it.arguments?.getInt("id")
            if (id != null) {
                EditNoteScreen(
                    navController = navController,
                    uiState = uiState,
                    onEvent = onEvent,
                    id = id
                )
            }
        }

        composable(
            route = Screen.NewNoteScreen.route
        ) {
            NewNoteScreen(
                navController = navController,
                uiState = uiState,
                onEvent = onEvent
            )
        }
    }
}