package com.devsquad.mind_melody.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Model.Audio;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {

    private List<Audio> audioList;
    private OnItemClickListener listener;
    private Audio defaultAudio = null;  // Save the current default audio
    private int currentPosition = -1;    // Currently playing audio position

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
        holder.audioImage.setImageResource(audio.getImageResId());

        // Setting the heart icon depending on whether the current audio is the default or not
        if (audio.equals(defaultAudio)) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);  // sincere love
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);  // hollow hearted love
        }

        // Click the heart button to set it as the default audio
        holder.favoriteButton.setOnClickListener(v -> {
            if (!audio.equals(defaultAudio)) {
                defaultAudio = audio;
                notifyDataSetChanged();

                // Update favouriteMusic in the database
                User loggedInUser = ((MyApplication) holder.itemView.getContext().getApplicationContext()).getLoggedInUser();
                UserDB userDB = UserDB.getDatabase(holder.itemView.getContext());
                UserDao userDao = userDB.userDao();

                new Thread(() -> {
                    userDao.updateFavouriteMusic(loggedInUser.getUserId(), audio.getFilePath());
                    loggedInUser.setFavouriteMusic(audio.getFilePath());  // Also update the user object in memory
                }).start();

                Toast.makeText(holder.itemView.getContext(), audio.getName() + " Has been set as default audio", Toast.LENGTH_SHORT).show();
            }
        });

        // Click Play Audio to update the current playing position
        holder.itemView.setOnClickListener(v -> {
            currentPosition = holder.getAdapterPosition();
            listener.onItemClick(audio);
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    // Setting the default audio and refreshing the adapter
    public void setDefaultAudio(Audio audio) {
        this.defaultAudio = audio;
        notifyDataSetChanged();  // Refresh the list and update the UI
    }

    // Returns the currently playing audio object
    public Audio getCurrentAudio() {
        if (currentPosition >= 0) {
            return audioList.get(currentPosition);
        }
        return null;
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView audioName;
        ImageView audioImage;
        ImageButton favoriteButton;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.audio_name);
            audioImage = itemView.findViewById(R.id.audio_image);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
        }
    }
}