package com.example.noteapplicationwithoutauth.feature_note.domain.repository

import com.example.noteapplicationwithoutauth.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow


interface NoteRepository {

    //this is interface as it is useful in creating the fake repositories and enhancing the testcases
    fun getNotes(): Flow<List<Note>>

    suspend fun getNotebyId(id: Int): Note?

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)
}