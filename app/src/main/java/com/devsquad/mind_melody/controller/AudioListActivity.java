package com.devsquad.mind_melody.controller;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

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

        // 初始化音频列表 并为每个音频设置对应的图片资源ID
        audioList = new ArrayList<>();
        audioList.add(new Audio("雨声", "android.resource://" + getPackageName() + "/" + R.raw.rain, R.drawable.rain_image));
        audioList.add(new Audio("森林", "android.resource://" + getPackageName() + "/" + R.raw.forest, R.drawable.forest_image));

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
            mediaPlayer = null;     // 避免空指针异常
        }
//        mediaPlayer = MediaPlayer.create(this, android.net.Uri.parse(audio.getFilePath()));
//        mediaPlayer.start();
        // 从filePath创建MediaPlayer
        mediaPlayer = MediaPlayer.create(this, android.net.Uri.parse(audio.getFilePath()));
        // 检查MediaPlayer是否成功创建
        if (mediaPlayer == null) {
            Log.e("AudioPlayback", "MediaPlayer无法创建，音频路径: " + audio.getFilePath());
            return;
        }

        Log.d("AudioPlayback", "正在播放音频: " + audio.getFilePath());

        mediaPlayer.start(); // 播放音频
        Log.i("AudioPlayback", "音频播放已启动");

        // 设置onCompletionListener，当音频播放完毕时调用
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.i("AudioPlayback", "音频播放完成");
            // 释放资源，防止内存泄漏
            mediaPlayer.release();
            mediaPlayer = null;
        });
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
