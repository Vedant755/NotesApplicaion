package com.example.noteapplicationwithoutauth.feature_note.data.repository

import com.example.noteapplicationwithoutauth.feature_note.data.data_source.NoteDao
import com.example.noteapplicationwithoutauth.feature_note.domain.model.Note
import com.example.noteapplicationwithoutauth.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepoImpl(
    private val dao: NoteDao
):NoteRepository{
    override fun getNotes(): Flow<List<Note>> {
        return dao.getNotes()
    }

    override suspend fun getNotebyId(id: Int): Note? {
        return dao.getNotebyId(id)
    }

    override suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
       dao.deleteNote(note)
    }

}