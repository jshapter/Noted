package com.example.noted.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.noted.data.NoteState
import com.example.noted.viewmodel.NoteEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: MutableStateFlow<NoteState>,
    notesMap: StateFlow<NoteState>,
    onEvent: (NoteEvent) -> (Unit)
) {

    val collectedUiState: State<NoteState> = uiState.collectAsState()
    val notesState: State<NoteState> = notesMap.collectAsState()

    val textState = remember { mutableStateOf(TextFieldValue(collectedUiState.value.content)) }
    val noteList = notesState.value.notes
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(NoteEvent.ShowNoteWriter)
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
            if (collectedUiState.value.isEditingNote) {
                NoteWriter(textState = textState, onEvent = onEvent)
            } else {
                if (noteList.isEmpty()) {
                    NoNotes()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(items = noteList, key = { it.id }) { note ->
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEvent(NoteEvent.ShowNoteEditor) }) {
                                NoteCard(note = note)
                            }
                            if (collectedUiState.value.isWritingNote) {
                                NoteEditor(note = note, textState = textState, onEvent = onEvent)
                            }
                        }
                    }
                }
            }

        }
    }
}
