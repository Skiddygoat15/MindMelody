package com.devsquad.mind_melody.Activities;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button btnPause, btnReplay, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        btnPause = findViewById(R.id.btnPause);
        btnReplay = findViewById(R.id.btnReplay);
        btnBack = findViewById(R.id.btnBack);

        int videoResId = getIntent().getIntExtra("videoResId", -1);
        if (videoResId != -1) {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(MediaPlayer::start);
        }

        btnPause.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
        });

        btnReplay.setOnClickListener(v -> videoView.start());

        btnBack.setOnClickListener(v -> finish());
    }
}
