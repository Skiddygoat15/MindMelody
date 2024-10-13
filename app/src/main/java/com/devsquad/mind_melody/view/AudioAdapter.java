package com.devsquad.mind_melody.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.model.Audio;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private List<Audio> audioList;
    private OnItemClickListener listener;
    private int currentPosition = -1;

    public interface OnItemClickListener {
        void onItemClick(Audio audio);
    }

    public AudioAdapter(List<Audio> audioList, OnItemClickListener listener) {
        this.audioList = audioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Audio audio = audioList.get(position);
        holder.audioName.setText(audio.getName());
        holder.itemView.setOnClickListener(v -> {
            currentPosition = holder.getAdapterPosition();
            listener.onItemClick(audio);
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public Audio getCurrentAudio() {
        return currentPosition >= 0 ? audioList.get(currentPosition) : null;
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView audioName;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.audio_name);
        }
    }
}