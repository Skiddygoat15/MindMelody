package com.devsquad.mind_melody.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.R;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.HistoryViewHolder>{
    private List<String> letters;

    // Constructor to receive the data list
    public ProfileAdapter(List<String> letters) {
        this.letters = letters;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_view, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String letter = letters.get(position);
        holder.letterText.setText(letter);
    }

    @Override
    public int getItemCount() {
        return letters.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView letterText;
        View solidCircle;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            letterText = itemView.findViewById(R.id.letterText);
            solidCircle = itemView.findViewById(R.id.solidCircle);
        }
    }
}
