package com.example.noted.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noted.data.Note
import com.example.noted.data.NoteDao
import com.example.noted.data.NotesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BaseViewModel(
    private val dao: NoteDao
): ViewModel() {

    private val _state = dao.getAllNotes().map { NotesState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotesState()
        )
    val state = _state

    private val _textState = MutableStateFlow(NotesState())
    val textState = _textState

    fun getNote(
        id: Int,
    ) : Note {
        return dao.getNote(id)
    }

}