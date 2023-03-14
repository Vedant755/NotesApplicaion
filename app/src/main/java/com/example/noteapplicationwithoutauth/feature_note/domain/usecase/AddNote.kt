package com.example.noteapplicationwithoutauth.feature_note.domain.usecase


import com.example.noteapplicationwithoutauth.feature_note.domain.model.InvalidNoteException
import com.example.noteapplicationwithoutauth.feature_note.domain.model.Note
import com.example.noteapplicationwithoutauth.feature_note.domain.repository.NoteRepository

class AddNote (
    private val repository: NoteRepository
    ){
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note){
      if (note.title.isBlank()){
          throw InvalidNoteException("The title cannot be empty")
      }
        if (note.content.isBlank()){
            throw InvalidNoteException("The Content cannot be empty")
        }
        repository.insertNote(note)
    }

}