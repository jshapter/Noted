package com.example.noted.data

data class NotesState(
    val notes: List<Note> = emptyList(),
    var id: Int? = 0,
    var content: String = "",
)
