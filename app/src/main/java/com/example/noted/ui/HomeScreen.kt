package com.example.noted.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noted.data.NoteState
import com.example.noted.viewmodel.NoteEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    uiState: MutableStateFlow<NoteState>,
    notesMap: StateFlow<NoteState>,
    onEvent: (NoteEvent) -> Unit
) {

    val uiStateCollected: State<NoteState> = uiState.collectAsState()
    val cachedNote = remember { uiStateCollected.value.cachedNote }
    Log.d(TAG, "State at Home : ${uiStateCollected.value}")

    val notesState: State<NoteState> = notesMap.collectAsState()
    val noteList = notesState.value.notes

    val snackbarHostState = remember { SnackbarHostState() }
    val noteDeletedSnackbar = uiStateCollected.value.noteDeleted

    if (noteDeletedSnackbar) {
        if (cachedNote != null) {
            onEvent(NoteEvent.SetId(cachedNote.id))
        }
        if (cachedNote != null) {
            onEvent(NoteEvent.SetContent(cachedNote.content))
        }
        LaunchedEffect(snackbarHostState) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "Note deleted",
                actionLabel = "UNDO",
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> onEvent(NoteEvent.ResetState)
                SnackbarResult.ActionPerformed -> onEvent(NoteEvent.SaveNote)
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
             TopAppBar(
                 colors = TopAppBarDefaults.mediumTopAppBarColors(
                     containerColor = MaterialTheme.colorScheme.inversePrimary,
                     navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                     titleContentColor = MaterialTheme.colorScheme.onBackground,
                     actionIconContentColor = MaterialTheme.colorScheme.onBackground

                 ),
                 navigationIcon = {
                     IconButton(onClick = { /*TODO*/ }) {
                         Icon(Icons.Default.Menu, contentDescription = "menu")
                     }
                 },
                 title = {
                     Text(text = "Noted")
                 },
                 scrollBehavior = scrollBehavior
             )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                onClick = {
                    onEvent(NoteEvent.ResetState)
                    navController.navigate(route = "new_note")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "new note")
            }
        }
    ) {PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (noteList.isEmpty()) {
                Text(text = "no notes to show...")
            } else {
                val scrollState = rememberLazyListState()
                LazyColumn(
                    state = scrollState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, top = 0.dp)
                ) {
                    items(items = noteList, key = { it.id }) { note ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onEvent(NoteEvent.ResetState)
                                onEvent(NoteEvent.SetId(note.id))
                                onEvent(NoteEvent.SetContent(note.content))
                                navController.navigate("edit_note/${note.id}")
                            }) {
                            NoteCard(note = note)
                        }
                    }
                }
            }
        }
    }
}
