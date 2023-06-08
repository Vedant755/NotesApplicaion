package com.example.noteapplicationwithoutauth.feature_note.domain.usecase

data class NotesUsecases (
    val getNotesUseCase: GetNotesUsecase,
    val deleteNodeUseCase: DeleteNotesCase,
    val addNote: AddNote,
    val getNote: GetNoteUseCase
    )