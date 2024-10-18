package com.devsquad.mind_melody.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;

import java.util.Date;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageButton btnPlayPause, btnBack;
    private TextView videoTitle;
    private boolean isPlaying = false;
    private User loggedInUser; // 存储登录用户信息
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        // 初始化控件
        videoView = findViewById(R.id.videoView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack = findViewById(R.id.backButton);
        videoTitle = findViewById(R.id.videoTitle);

        // 获取全局用户数据
        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();

        // **检查用户是否成功获取**
        if (loggedInUser != null) {
            Log.d("VideoPlayerActivity", "User ID: " + loggedInUser.getUserId());
        } else {
            Log.e("VideoPlayerActivity", "Logged-in user is null!");
        }

        // 获取数据库实例
        userDao = UserDB.getDatabase(this).userDao();

        // 获取传递的视频资源 ID 和标题
        int videoResId = getIntent().getIntExtra("videoResId", -1);
        String title = getIntent().getStringExtra("videoTitle");
        videoTitle.setText(title != null ? title : "Meditation Video");

        if (videoResId != -1) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
            videoView.setVideoURI(videoUri);
        }

        // 检查电量是否低于 20%
        if (isBatteryLow()) {
            showBatteryWarningDialog();
        } else {
            startVideoAndUpdateMeditationTime();
        }

        // 设置视频比例自适应监听器
        videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            float videoProportion = (float) videoWidth / videoHeight;

            int screenWidth = videoView.getWidth();
            int screenHeight = videoView.getHeight();
            float screenProportion = (float) screenWidth / screenHeight;

            if (videoProportion > screenProportion) {
                videoView.getLayoutParams().width = screenWidth;
                videoView.getLayoutParams().height = (int) (screenWidth / videoProportion);
            } else {
                videoView.getLayoutParams().height = screenHeight;
                videoView.getLayoutParams().width = (int) (screenHeight * videoProportion);
            }

            videoView.requestLayout();
            mp.start();
        });

        // 播放/暂停按钮逻辑
        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                videoView.pause();
                btnPlayPause.setImageResource(R.drawable.play_icon);
            } else {
                videoView.start();
                btnPlayPause.setImageResource(R.drawable.pause_icon);
            }
            isPlaying = !isPlaying;
        });

        // 返回按钮逻辑
        btnBack.setOnClickListener(v -> finish());
    }

    // 检查电量是否低于 20%
    private boolean isBatteryLow() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return batteryPct <= 0.2;
    }

    // 显示电量不足的警告弹窗
    private void showBatteryWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Low Battery")
                .setMessage("Your battery is below 20%. Do you want to play the video?")
                .setPositiveButton("Yes", (dialog, which) -> startVideoAndUpdateMeditationTime())
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // 返回到冥想视频列表界面
                })
                .show();
    }

    // 播放视频并更新冥想时间
    private void startVideoAndUpdateMeditationTime() {
        videoView.start();
        btnPlayPause.setImageResource(R.drawable.pause_icon);
        isPlaying = true;

        Date currentDate = new Date();

        new Thread(() -> {
            try {
                userDao.updateLastMeditDate(loggedInUser.getUserId(), currentDate);
                Log.d("VideoPlayerActivity", "Meditation date updated successfully.");
            } catch (Exception e) {
                Log.e("VideoPlayerActivity", "Error updating meditation date: " + e.getMessage());
            }
        }).start();
    }

}
