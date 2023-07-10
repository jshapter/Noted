package com.example.noted.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.noted.data.NoteState
import com.example.noted.viewmodel.NoteEvent
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNoteScreen(
    navController: NavController,
    uiState: MutableStateFlow<NoteState>,
    onEvent: (NoteEvent) -> Unit
) {
    val collectedUiState: State<NoteState> = uiState.collectAsState()
    Log.d(TAG, "State at NewNote : ${collectedUiState.value}")
    val textState = remember { mutableStateOf(TextFieldValue(collectedUiState.value.content)) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        onEvent(NoteEvent.ResetState)
                        navController.navigate(route = "home")
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
                    if (textState.value.text != "") {
                        IconButton(onClick = {
                            onEvent(NoteEvent.SetContent(textState.value.text))
                            onEvent(NoteEvent.SaveNote)

                            navController.navigate(route = "home")
                        }
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "save note"
                            )
                        }
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
                placeholder = {
                    Text(
                        text = "New note",
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.alpha(0.8f)
                    )
                },
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