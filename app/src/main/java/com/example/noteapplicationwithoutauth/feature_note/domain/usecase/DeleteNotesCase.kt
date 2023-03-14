package com.example.noteapplicationwithoutauth.feature_note.domain.usecase

import com.example.noteapplicationwithoutauth.feature_note.domain.model.Note


import com.example.noteapplicationwithoutauth.feature_note.domain.repository.NoteRepository

class DeleteNotesCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note){
        noteRepository.deleteNote(note)
    }
}