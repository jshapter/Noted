package com.example.noted.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.data.DataRepository
import com.example.noted.data.Note
import com.example.noted.data.NoteDao
import com.example.noted.data.NoteState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BaseViewModel(
    dao: NoteDao
): ViewModel() {

    private val repo = DataRepository(dao)

    private val _notesMap = repo.getAllNotes().map { NoteState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoteState()
        )
    val notesMap = _notesMap

    private val _uiState = MutableStateFlow(NoteState())
    val uiState = _uiState

    fun onEvent(event: NoteEvent) {
        when(event) {

            is NoteEvent.DeleteNote -> {
                _uiState.update { it.copy(
                    cachedNote = event.note
                ) }
                viewModelScope.launch {
                    repo.deleteNote(event.note)
                }
            }

            NoteEvent.SaveNote -> {
                val content = _uiState.value.content
                val id = _uiState.value.id
                if(content.isBlank()) {
                    return
                }
                val note = id?.let {
                    Note(
                        id = it,
                        content = content
                    )
                }
                _uiState.update { it.copy(
                    content = ""
                ) }
                viewModelScope.launch {
                    if (note != null) {
                        repo.upsertNote(note)
                    }
                }
            }

            is NoteEvent.SetContent -> {
                _uiState.update { it.copy(
                    content = event.content
                    ) }
            }

            is NoteEvent.SetId -> {
                _uiState.update { it.copy(
                    id = event.id
                    ) }
            }

            is NoteEvent.NoteDeleted -> {
                _uiState.update { it.copy(
                    noteDeletedSnack = event.noteDeletedSnack
                ) }
            }

            is NoteEvent.UndoDelete -> {
                val note = _uiState.value.cachedNote
                viewModelScope.launch {
                    if (note != null) {
                        repo.upsertNote(note)
                    }
                }
                _uiState.update { it.copy(
                    noteDeletedSnack = false
                ) }
            }

            is NoteEvent.GetNote -> {
                viewModelScope.launch {
                    val selectedNote = event.id?.let { repo.getNote(it) }
                    _uiState.update { it.copy(
                        selectedNote = selectedNote
                    ) }
                }
            }
        }
    }
}