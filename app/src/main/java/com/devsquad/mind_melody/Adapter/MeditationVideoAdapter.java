package com.devsquad.mind_melody.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Model.Video.VideoItem;
import com.devsquad.mind_melody.R;

import java.util.List;

public class MeditationVideoAdapter extends RecyclerView.Adapter<MeditationVideoAdapter.VideoViewHolder> {

    private final List<VideoItem> videoList;
    private final OnVideoClickListener listener;

    public MeditationVideoAdapter(List<VideoItem> videoList, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        holder.titleTextView.setText(video.getTitle());
        holder.thumbnailImageView.setImageResource(video.getThumbnailResId());
        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImageView;
        TextView titleTextView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.videoThumbnail);
            titleTextView = itemView.findViewById(R.id.videoTitle);
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem video);
    }
}
