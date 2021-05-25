package com.example.mynotes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mynotes.models.Note;

import java.util.List;

@Dao
public interface NoteDao{

    @Insert
    void insertNote(Note note);

    @Query("SELECT * FROM NOTES_TABLE")
    LiveData<List<Note>> getAllNotes();

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);
}
