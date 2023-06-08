package com.example.noteapplicationwithoutauth.feature_note.data.data_source

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapplicationwithoutauth.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteDao {

    @Query(value = "SELECT * FROM note")// this gives all the notes
    fun getNotes(): Flow<List<Note>>

    @Query(value= "SELECT * FROM note WHERE id= :id")
    suspend fun getNotebyId(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}