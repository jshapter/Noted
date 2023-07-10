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

            is NoteEvent.GetNote -> {
                viewModelScope.launch {
                    val cachedNote = repo.getNote(id = event.id)
                    _uiState.update { it.copy(
                        cachedNote = cachedNote
                    ) }
                }
            }

            is NoteEvent.SetId -> {
                _uiState.update { it.copy(
                    id = event.id
                ) }
            }

            is NoteEvent.SetContent -> {
                _uiState.update { it.copy(
                    content = event.content
                ) }
            }

            NoteEvent.ResetState -> {
                _uiState.update { it.copy(
                    id = 0,
                    content = "",
                    cachedNote = null,
                    noteDeleted = false
                ) }
            }

            is NoteEvent.DeleteNote -> {
                _uiState.update { it.copy(
                    cachedNote = _uiState.value.id?.let { it1 -> Note(
                        id = it1,
                        content = _uiState.value.content
                    ) }
                ) }
                viewModelScope.launch {
                    repo.deleteNote(event.note)
                }
                _uiState.update { it.copy(
                    content = "",
                    id = 0,
                    noteDeleted = true
                ) }
            }

            NoteEvent.SaveNote -> {
                if(_uiState.value.content.isBlank()) {
                    return
                }
                val note = _uiState.value.id?.let {
                    Note(
                        id = it,
                        content = _uiState.value.content
                    )
                }
                _uiState.update { it.copy(
                    content = "",
                    id = 0,
                    cachedNote = null,
                    noteDeleted = false
                ) }
                viewModelScope.launch {
                    if (note != null) {
                        repo.upsertNote(note)
                    }
                }
            }
        }
    }
}