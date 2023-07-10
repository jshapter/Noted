package com.example.noted.data

data class NoteState(
    val notes: List<Note> = emptyList(),
    val id: Int? = 0,
    val content: String = "",
    val cachedNote: Note? = null,
    var noteDeleted: Boolean = false
)
