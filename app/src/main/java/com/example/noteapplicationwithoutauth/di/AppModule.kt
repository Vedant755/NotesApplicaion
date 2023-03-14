package com.example.noteapplicationwithoutauth.di

import android.app.Application
import androidx.room.Room
import com.example.noteapplicationwithoutauth.feature_note.data.data_source.NoteDatabase
import com.example.noteapplicationwithoutauth.feature_note.data.repository.NoteRepoImpl
import com.example.noteapplicationwithoutauth.feature_note.domain.repository.NoteRepository
import com.example.noteapplicationwithoutauth.feature_note.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesNoteDatabase(app: Application): NoteDatabase{
        return Room.databaseBuilder(
            app,NoteDatabase::class.java,NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesNoteRepository(db: NoteDatabase): NoteRepository{
        return NoteRepoImpl(dao = db.noteDao)
    }

    @Provides
    @Singleton
    fun providesNoteUsecases(repository: NoteRepository): NotesUsecases{
        return NotesUsecases(
            getNotesUseCase = GetNotesUsecase(repository),
            deleteNodeUseCase = DeleteNotesCase(repository),
            addNote = AddNote(repository),
            getNote = GetNoteUseCase(repository)
        )
    }
}