package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Model.Audio;
import com.devsquad.mind_melody.Adapter.AudioAdapter;

import java.util.ArrayList;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private List<Audio> audioList;
    private AudioAdapter adapter;
    private ImageButton playPauseButton, backButton, focusModeButton;
    private boolean isPlaying = false; // 标识当前是否正在播放
    private int lastPosition = 0; // 保存暂停时的位置
    private Audio currentAudio = null; // 当前播放的音频
    private User loggedInUser; // 当前登录的用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        // RecyclerView设置
        RecyclerView recyclerView = findViewById(R.id.audio_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 添加分隔线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        // 初始化播放/暂停按钮和停止按钮
        playPauseButton = findViewById(R.id.play_button);

        // 初始化返回按钮并设置点击事件
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 AudioListActivity
            Intent intent = new Intent(AudioListActivity.this, HomeActivity.class);
            // 启动 AudioListActivity
            startActivity(intent);
        });

        // 初始化专注模式按钮并设置点击事件
        focusModeButton = findViewById(R.id.focus_mode_button);
        focusModeButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 FocusModeActivity
            Intent intent = new Intent(AudioListActivity.this, FocusModeActivity.class);
            startActivity(intent);
        });

        // 初始化音频列表 并为每个音频设置对应的图片资源ID
        audioList = new ArrayList<>();
        audioList.add(new Audio("Rain", "android.resource://" + getPackageName() + "/" + R.raw.rain, R.drawable.rain_image));
        audioList.add(new Audio("Forest", "android.resource://" + getPackageName() + "/" + R.raw.forest, R.drawable.forest_image));
        audioList.add(new Audio("Sea", "android.resource://" + getPackageName() + "/" + R.raw.sea, R.drawable.sea_image));
        audioList.add(new Audio("Wind", "android.resource://" + getPackageName() + "/" + R.raw.wind, R.drawable.wind_image));
        audioList.add(new Audio("Piano", "android.resource://" + getPackageName() + "/" + R.raw.piano, R.drawable.piano_image));
        audioList.add(new Audio("Quiet", "android.resource://" + getPackageName() + "/" + R.raw.quiet, R.drawable.quiet_image));
        audioList.add(new Audio("Cafe", "android.resource://" + getPackageName() + "/" + R.raw.coffee_shop, R.drawable.cafe_image));

        adapter = new AudioAdapter(audioList, this::handleAudioItemClick);
        recyclerView.setAdapter(adapter);

        // 从数据库中获取 favouriteMusic 并设置到 adapter
        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();
        UserDB userDB = UserDB.getDatabase(this);
        UserDao userDao = userDB.userDao();

        // 异步获取数据库中的 favouriteMusic
        new Thread(() -> {
            String favouriteMusic = userDao.getFavouriteMusic(loggedInUser.getUserId());
            runOnUiThread(() -> {
                // 设置默认的favorite audio
                if (favouriteMusic != null) {
                    for (Audio audio : audioList) {
                        if (audio.getFilePath().equals(favouriteMusic)) {
                            adapter.setDefaultAudio(audio);  // 设置默认音频
                        }
                    }
                }
            });
        }).start();

        playPauseButton.setOnClickListener(v -> {
            if (currentAudio == null) {
                // 如果没有选择音频，提示用户选择
                Toast.makeText(this, "Please select an audio before playing.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPlaying) {
                // 如果当前没有播放，继续播放
                playAudio(currentAudio);
                playPauseButton.setImageResource(R.drawable.pause_icon);  // 切换为暂停图标
                isPlaying = true;
            } else {
                // 如果当前正在播放，暂停音频
                pauseAudio();
                playPauseButton.setImageResource(R.drawable.play_icon);  // 切换为播放图标
                isPlaying = false;
            }
        });
    }

    // AudioListActivity.java 中，onCreate 方法之后
    private void handleAudioItemClick(Audio audio) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();  // 停止当前播放的音频
            mediaPlayer.reset();  // 重置 MediaPlayer，准备播放新的音频
        }

        currentAudio = audio;  // 更新为点击的音频
        playAudio(currentAudio);  // 播放新的音频
        playPauseButton.setImageResource(R.drawable.pause_icon);  // 切换为暂停图标
        isPlaying = true;  // 更新播放状态
    }


    // 播放音频
    private void playAudio(Audio audio) {
        // 如果正在播放其他音频，先停止播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();  // 停止当前音频
            mediaPlayer.reset();  // 重置 MediaPlayer
        }

        // 初始化MediaPlayer
        mediaPlayer = MediaPlayer.create(this, android.net.Uri.parse(audio.getFilePath()));
        mediaPlayer.setLooping(true);  // 设置音频循环播放

        // 如果之前已经暂停，继续播放
        mediaPlayer.seekTo(lastPosition); // 从上次暂停的位置继续播放
        mediaPlayer.start(); // 播放音频
        Log.i("AudioPlayback", "音频播放已启动");

        // 设置当音频播放完毕时的监听器，循环播放音频
        mediaPlayer.setOnCompletionListener(mp -> {
            Log.i("AudioPlayback", "音频播放完成，重新开始播放");
            mp.seekTo(0); // 从头开始
            mp.start();  // 重新播放
        });
    }

    // 暂停音频
    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            lastPosition = mediaPlayer.getCurrentPosition(); // 保存暂停时的位置
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 当页面离开时，暂停音频并重置 MediaPlayer
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pauseAudio();  // 暂停当前音频
        }
        if (mediaPlayer != null) {
            mediaPlayer.reset();  // 重置 MediaPlayer
            mediaPlayer.release(); // 释放 MediaPlayer 资源
            mediaPlayer = null;    // 防止内存泄漏，清除 MediaPlayer 对象
        }
    }

}
