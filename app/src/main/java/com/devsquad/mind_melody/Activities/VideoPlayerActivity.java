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
    private User loggedInUser; 
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        
        videoView = findViewById(R.id.videoView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnBack = findViewById(R.id.backButton);
        videoTitle = findViewById(R.id.videoTitle);

        
        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();

        
        if (loggedInUser != null) {
            Log.d("VideoPlayerActivity", "User ID: " + loggedInUser.getUserId());
        } else {
            Log.e("VideoPlayerActivity", "Logged-in user is null!");
        }

        
        userDao = UserDB.getDatabase(this).userDao();

        
        int videoResId = getIntent().getIntExtra("videoResId", -1);
        String title = getIntent().getStringExtra("videoTitle");
        videoTitle.setText(title != null ? title : "Meditation Video");

        if (videoResId != -1) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
            videoView.setVideoURI(videoUri);
        }

        // Check if the battery level is below 20%
        if (isBatteryLow()) {
            showBatteryWarningDialog();
        } else {
            startVideoAndUpdateMeditationTime();
        }

        // Set a listener for adaptive video aspect ratio
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

        // play/pause button
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

        
        btnBack.setOnClickListener(v -> finish());
    }

    // Check if the battery level is below 20%
    private boolean isBatteryLow() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return batteryPct <= 0.2;
    }

    // Display a low battery warning popup
    private void showBatteryWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Low Battery")
                .setMessage("Your battery is below 20%. Do you want to play the video?")
                .setPositiveButton("Yes", (dialog, which) -> startVideoAndUpdateMeditationTime())
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); 
                })
                .show();
    }

    // Play the video and update the meditation time
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
