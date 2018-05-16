package com.hardstudio.hasthi.notes.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hardstudio.hasthi.notes.Models.NoteDetails;
import com.hardstudio.hasthi.notes.NoteDetailsActivity;
import com.hardstudio.hasthi.notes.NotesActivity;
import com.hardstudio.hasthi.notes.R;
import com.hardstudio.hasthi.notes.Util.Constants;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private Context context;
    private List<NoteDetails> note;

    public NotesAdapter(Context context, List<NoteDetails> note) {
        this.context = context;
        this.note = note;
    }


    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_block, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(NotesAdapter.ViewHolder holder, final int position) {
        holder.noteTitle.setText(note.get(position).getTitle());
        holder.noteBody.setText(note.get(position).getBody());
        holder.noteBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, NoteDetailsActivity.class)
                        .putExtra(Constants.NOTE_PROCESS, Constants.EDIT_NOTE_VALUE)
                        .putExtra(Constants.NOTE_ID_KEY, note.get(position).getNoteID()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return note.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView noteTitle;
        public TextView noteBody;
        public ConstraintLayout noteBlock;




        public ViewHolder(View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteAdapterBlockTitle);
            noteBody = itemView.findViewById(R.id.noteAdapterBlockBody);
            noteBlock = itemView.findViewById(R.id.noteBlock);

        }

    }

}
