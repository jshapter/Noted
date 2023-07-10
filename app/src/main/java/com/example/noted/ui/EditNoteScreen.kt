package com.example.noted.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noted.data.NoteState
import com.example.noted.viewmodel.NoteEvent
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    navController: NavController,
    uiState: MutableStateFlow<NoteState>,
    onEvent: (NoteEvent) -> Unit,
    id: Int
) {
    onEvent(NoteEvent.GetNote(id))

    val collectedUiState: State<NoteState> = uiState.collectAsState()

    val note = collectedUiState.value.cachedNote

    val textState = remember { mutableStateOf(TextFieldValue(text = collectedUiState.value.content, selection = TextRange(
        collectedUiState.value.content.length))) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(NoteEvent.ResetState)
                        navController.navigate(route = "home") {
                            popUpTo(route = "home") {
                                inclusive = true
                            }
                        }
                    }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "back"
                        )
                    }
                },
                title = {
                    Text(
                        text = ""
                    )
                },
                actions = {
                    IconButton(onClick = {
                        onEvent(NoteEvent.SetContent(textState.value.text))
                        onEvent(NoteEvent.SaveNote)

                        navController.navigate(route = "home") {
                            popUpTo(route = "home") {
                                inclusive = true
                            }
                        }
                    }
                    ) {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = "save note"
                        )
                    }
                    IconButton(onClick = {
                        note?.let { NoteEvent.DeleteNote(it) }?.let { onEvent(it) }

                        navController.navigate(route = "home") {
                            popUpTo(route = "home") {
                                inclusive = true
                            }
                        }
                    }
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "delete note"
                        )
                    }
                }
            )
        }
    ) { PaddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues)
        ) {
            val focusRequester = remember { FocusRequester() }
            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = textState.value,
                onValueChange = { textState.value = it },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.background,
                    unfocusedBorderColor = MaterialTheme.colorScheme.background
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}