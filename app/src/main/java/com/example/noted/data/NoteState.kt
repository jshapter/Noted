package com.example.noted.data

data class NoteState(
    val notes: List<Note> = emptyList(),
    var id: Int? = 0,
    var content: String = "",
    var noteDeletedSnack: Boolean = false,
    val cachedNote: Note? = null,
    val selectedNote: Note? = null
)
