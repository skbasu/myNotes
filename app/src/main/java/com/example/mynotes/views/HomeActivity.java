package com.example.mynotes.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.adapters.NotesAdapter;
import com.example.mynotes.models.Note;
import com.example.mynotes.viewmodels.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NotesAdapter notesAdapter;
    private Dialog addNoteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RecyclerView notesContainer = findViewById(R.id.notesContainer);
        notesAdapter = new NotesAdapter();
        notesContainer.setLayoutManager(new LinearLayoutManager(this));
        notesContainer.setAdapter(notesAdapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);
            }
        });

        FloatingActionButton addNote = findViewById(R.id.addNote);
        addNoteDialog = new Dialog(this);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddNoteDialog();
            }
        });

    }

    public void openAddNoteDialog() {
        addNoteDialog.setContentView(R.layout.add_note_dialog);
        addNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addNoteDialog.setCancelable(false);
        ImageView close_icon = addNoteDialog.findViewById(R.id.close_icon);
        TextInputLayout editNoteTitle = addNoteDialog.findViewById(R.id.editNoteTitle);
        TextInputLayout editNoteDescription = addNoteDialog.findViewById(R.id.editNoteDescription);
        Button saveNoteBtn = addNoteDialog.findViewById(R.id.saveNoteBtn);
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteDialog.dismiss();
            }
        });
        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editNoteTitle.getEditText().getText().toString().trim();
                String description = editNoteDescription.getEditText().getText().toString().trim();
                if (title.isEmpty()){
                    editNoteTitle.setError("Please enter a Title");
                    editNoteTitle.requestFocus();
                    return;
                }
                if (description.isEmpty()){
                    editNoteDescription.setError("Please enter a Description");
                    editNoteDescription.requestFocus();
                    return;
                }
                DateFormat dateFormat = DateFormat.getDateInstance();
                Calendar calendar = Calendar.getInstance();
                String noteDate = dateFormat.format(calendar.getTime());
                Note note = new Note(title, description, noteDate);
                noteViewModel.insert(note);
                addNoteDialog.dismiss();
                Toast insertedToast = Toast.makeText(v.getContext(), "New Note Inserted", Toast.LENGTH_SHORT);
                insertedToast.setGravity(Gravity.CENTER, 0, 0);
                insertedToast.show();
            }
        });
        addNoteDialog.show();
    }
}