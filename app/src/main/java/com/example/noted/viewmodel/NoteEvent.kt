package com.example.noted.viewmodel

import com.example.noted.data.Note

sealed interface NoteEvent {
    object SaveNote: NoteEvent
    data class SetContent(val content: String): NoteEvent
    data class SetId(val id: Int): NoteEvent

    data class DeleteNote(val note: Note): NoteEvent

    data class GetNote(val id: Int): NoteEvent

    object ResetState: NoteEvent
}