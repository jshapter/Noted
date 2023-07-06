package com.example.noted.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val dao: NoteDao
): ViewModel() {

    private val _notesMap = dao.getAllNotes().map { NoteState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoteState()
        )
    val notesMap = _notesMap

    private val _uiState = MutableStateFlow(NoteState())
    val uiState = _uiState

    fun getNote(
        id: Int,
    ) : Note {
        return dao.getNote(id)
    }

    fun onEvent(event: NoteEvent) {
        when(event) {

            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
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
                viewModelScope.launch {
                    if (note != null) {
                        dao.upsertNote(note)
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
        }
    }
}