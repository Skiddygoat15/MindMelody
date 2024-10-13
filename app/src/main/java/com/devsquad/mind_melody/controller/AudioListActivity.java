package com.devsquad.mind_melody.controller;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.model.Audio;
import com.devsquad.mind_melody.view.AudioAdapter;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private List<Audio> audioList;
    private AudioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        // 初始化音频列表
        audioList = new ArrayList<>();
        audioList.add(new Audio("雨声", "android.resource://" + getPackageName() + "/" + R.raw.rain));
        audioList.add(new Audio("森林", "android.resource://" + getPackageName() + "/" + R.raw.forest));

        // RecyclerView设置
        RecyclerView recyclerView = findViewById(R.id.audio_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioAdapter(audioList, this::playAudio);
        recyclerView.setAdapter(adapter);

        // 播放按钮
        findViewById(R.id.play_button).setOnClickListener(v -> playAudio(adapter.getCurrentAudio()));
        findViewById(R.id.pause_button).setOnClickListener(v -> pauseAudio());
        findViewById(R.id.stop_button).setOnClickListener(v -> stopAudio());
    }

    // 播放音频
    private void playAudio(Audio audio) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, android.net.Uri.parse(audio.getFilePath()));
        mediaPlayer.start();
    }

    // 暂停音频
    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // 停止音频
    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
