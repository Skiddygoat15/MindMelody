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
    private Audio defaultAudio = null;  // 保存当前默认音频
    private int currentPosition = -1;    // 当前播放的音频位置

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

        // 根据当前音频是否为默认，设置爱心图标
        if (audio.equals(defaultAudio)) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite);  // 实心爱心
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);  // 空心爱心
        }

        // 点击爱心按钮，设置为默认音频
        holder.favoriteButton.setOnClickListener(v -> {
            if (!audio.equals(defaultAudio)) {
                defaultAudio = audio;
                notifyDataSetChanged();  // 通知列表刷新

                // 更新数据库中的 favouriteMusic
                User loggedInUser = ((MyApplication) holder.itemView.getContext().getApplicationContext()).getLoggedInUser();
                UserDB userDB = UserDB.getDatabase(holder.itemView.getContext());
                UserDao userDao = userDB.userDao();

                // 更新数据库
                new Thread(() -> {
                    userDao.updateFavouriteMusic(loggedInUser.getUserId(), audio.getFilePath());
                    loggedInUser.setFavouriteMusic(audio.getFilePath());  // 同时更新内存中的用户对象
                }).start();

                Toast.makeText(holder.itemView.getContext(), audio.getName() + " Has been set as default audio", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击播放音频，更新当前播放的位置
        holder.itemView.setOnClickListener(v -> {
            currentPosition = holder.getAdapterPosition();
            listener.onItemClick(audio);
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    // 设置默认音频并刷新适配器
    public void setDefaultAudio(Audio audio) {
        this.defaultAudio = audio;
        notifyDataSetChanged();  // 刷新列表，更新UI
    }

    // 返回当前播放的音频对象
    public Audio getCurrentAudio() {
        if (currentPosition >= 0) {
            return audioList.get(currentPosition);
        }
        return null;
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView audioName;
        ImageView audioImage;
        ImageButton favoriteButton;  // 爱心按钮

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            audioName = itemView.findViewById(R.id.audio_name);
            audioImage = itemView.findViewById(R.id.audio_image);
            favoriteButton = itemView.findViewById(R.id.favorite_button);  // 绑定爱心按钮
        }
    }
}