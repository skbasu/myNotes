package com.example.mynotes.adapters;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynotes.R;
import com.example.mynotes.models.Note;
import com.example.mynotes.viewmodels.NoteViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {

    List<Note> notes = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note, parent, false);
        return new NotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        holder.noteTitle.setText(notes.get(position).getNoteTitle());
        holder.noteDate.setText(notes.get(position).getNoteDate());
        holder.noteDescription.setText(notes.get(position).getNoteDescription());

        boolean isExpanded = notes.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowIcon.setImageResource(isExpanded ? R.drawable.down_icon : R.drawable.side_icon);
    }

    public void setNotes(List<Note> notes){
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getNoteAt(int position){
        return notes.get(position);
    }

    public class NotesHolder extends RecyclerView.ViewHolder{

        TextView noteTitle;
        TextView noteDate;
        TextView noteDescription;
        ImageView arrowIcon, operationsBtn;
        LinearLayout expandableLayout, clickToShow;

        public NotesHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDate = itemView.findViewById(R.id.noteDate);
            noteDescription = itemView.findViewById(R.id.noteDescription);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            clickToShow = itemView.findViewById(R.id.clickToShow);
            clickToShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Note note = notes.get(getAdapterPosition());
                    note.setExpanded(!note.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            operationsBtn = itemView.findViewById(R.id.operationsBtn);
            operationsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOperationsBottomSheet(v);
                }
            });
        }

        public void showOperationsBottomSheet(View view){
            BottomSheetDialog operationsBottomSheet = new BottomSheetDialog(view.getContext(), R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(view.getContext()).inflate(
                    R.layout.operations_bottomsheet, (LinearLayout) view.findViewById(R.id.optionsBottomSheetContainer)
            );
            LinearLayout copyOption = bottomSheetView.findViewById(R.id.copyOption);
            LinearLayout shareOption = bottomSheetView.findViewById(R.id.shareOption);
            LinearLayout editOption = bottomSheetView.findViewById(R.id.editOption);
            LinearLayout deleteOption = bottomSheetView.findViewById(R.id.deleteOption);
            NoteViewModel noteViewModel = new ViewModelProvider((ViewModelStoreOwner) view.getContext()).get(NoteViewModel.class);
            copyOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData data = ClipData.newPlainText("description", notes.get(getAdapterPosition()).getNoteDescription());
                    clipboardManager.setPrimaryClip(data);
                    data.getDescription();
                    Toast copiedToast = Toast.makeText(v.getContext(), "Note Text Copied", Toast.LENGTH_SHORT);
                    copiedToast.setGravity(Gravity.CENTER, 0, 0);
                    copiedToast.show();
                    operationsBottomSheet.dismiss();
                }
            });
            shareOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareText = new Intent(Intent.ACTION_SEND);
                    shareText.setType("text/plain");
                    shareText.putExtra(Intent.EXTRA_TEXT, notes.get(getAdapterPosition()).getNoteDescription());
                    itemView.getContext().startActivity(Intent.createChooser(shareText, "Share Text"));
                    operationsBottomSheet.dismiss();
                }
            });
            editOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog editDialog = new Dialog(v.getContext());
                    editDialog.setContentView(R.layout.edit_note_dialog);
                    editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    editDialog.setCancelable(false);
                    ImageView editClose_icon = editDialog.findViewById(R.id.editClose_icon);
                    TextInputLayout noteTitleEdit = editDialog.findViewById(R.id.noteTitleEdit);
                    TextInputLayout noteDescriptionEdit = editDialog.findViewById(R.id.noteDescriptionEdit);
                    noteTitleEdit.getEditText().setText(notes.get(getAdapterPosition()).getNoteTitle());
                    noteDescriptionEdit.getEditText().setText(notes.get(getAdapterPosition()).getNoteDescription());
                    Button saveBtn = editDialog.findViewById(R.id.saveBtn);
                    editClose_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                        }
                    });
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String editedTitle = noteTitleEdit.getEditText().getText().toString().trim();
                            String editedDescription = noteDescriptionEdit.getEditText().getText().toString().trim();
                            if (editedTitle.isEmpty()){
                                noteTitleEdit.setError("Please enter a Title");
                                noteTitleEdit.requestFocus();
                                return;
                            }
                            if (editedDescription.isEmpty()){
                                noteDescriptionEdit.setError("Please enter a Description");
                                noteDescriptionEdit.requestFocus();
                                return;
                            }
                            DateFormat dateFormatEdit = DateFormat.getDateInstance();
                            Calendar calendarEdit = Calendar.getInstance();
                            String noteDateEdited = dateFormatEdit.format(calendarEdit.getTime());
                            Note editedNote = new Note(editedTitle, editedDescription, noteDateEdited);
                            editedNote.setId(notes.get(getAdapterPosition()).getId());
                            noteViewModel.update(editedNote);
                            Toast updatedToast = Toast.makeText(v.getContext(), "Note Updated", Toast.LENGTH_SHORT);
                            updatedToast.setGravity(Gravity.CENTER, 0, 0);
                            updatedToast.show();
                            editDialog.dismiss();
                        }
                    });
                    operationsBottomSheet.dismiss();
                    editDialog.show();
                }
            });
            deleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog deleteDialog = new Dialog(v.getContext());
                    deleteDialog.setContentView(R.layout.delete_note_dialog);
                    deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ImageView deleteClose_icon = deleteDialog.findViewById(R.id.deleteClose_icon);
                    TextView deletedNoteTitle = deleteDialog.findViewById(R.id.deletedNoteTitle);
                    Button proceedBtn = deleteDialog.findViewById(R.id.proceedBtn);
                    deleteClose_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteDialog.dismiss();
                        }
                    });
                    deletedNoteTitle.setText(notes.get(getAdapterPosition()).getNoteTitle());
                    proceedBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            noteViewModel.delete(getNoteAt(getAdapterPosition()));
                            Toast deletedToast = Toast.makeText(v.getContext(), "Note Deleted", Toast.LENGTH_SHORT);
                            deletedToast.setGravity(Gravity.CENTER, 0, 0);
                            deletedToast.show();
                            deleteDialog.dismiss();
                        }
                    });
                    deleteDialog.show();
                    operationsBottomSheet.dismiss();
                }
            });
            operationsBottomSheet.setContentView(bottomSheetView);
            operationsBottomSheet.show();
        }
    }
}
