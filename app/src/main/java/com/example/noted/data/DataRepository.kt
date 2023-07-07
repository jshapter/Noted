package com.example.noted.data

import kotlinx.coroutines.flow.Flow

class DataRepository(private val dao: NoteDao) {

    suspend fun upsertNote(note: Note) = dao.upsertNote(note)

    suspend fun deleteNote(Note: Note) = dao.deleteNote(Note)

    fun getAllNotes(): Flow<List<Note>> = dao.getAllNotes()

    suspend fun getNote(id: Int) = dao.getNote(id)

}