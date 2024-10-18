package com.devsquad.mind_melody.Activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import com.devsquad.mind_melody.R;

public class FocusModeActivity extends AppCompatActivity {

    private TextView timerText;
    private Button pauseButton, stopButton, extendButton, changeSoundButton, customMusicButton;
    private ImageButton backButton;
    private SeekBar volumeSeekBar;
    private Spinner scenarioSpinner;
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private ExoPlayer player;

    // Variables for session control
    private long remainingTime = 25 * 60 * 1000; // 25 minutes
    private long extendTime = 5 * 60 * 1000; // Extend by 5 minutes
    private String currentSound = "white_noise"; // Default sound

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        // Initialize UI components
        timerText = findViewById(R.id.timer_text);
        pauseButton = findViewById(R.id.button_pause);
        stopButton = findViewById(R.id.button_stop);
        extendButton = findViewById(R.id.button_extend);
        changeSoundButton = findViewById(R.id.button_change_sound);
        customMusicButton = findViewById(R.id.button_custom_music);
        volumeSeekBar = findViewById(R.id.seekbar_volume);
        scenarioSpinner = findViewById(R.id.spinner_scenario);
        backButton = findViewById(R.id.back_button);

        // Initialize ExoPlayer for background sounds
        player = new ExoPlayer.Builder(this).build();
        playBackgroundSound(currentSound);

        // Set timer for 25 minutes
        startFocusTimer(remainingTime);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(FocusModeActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Pause button logic
        pauseButton.setOnClickListener(v -> {
            if (!isPaused) {
                countDownTimer.cancel();
                player.pause();
                isPaused = true;
                pauseButton.setText("Resume");
            } else {
                startFocusTimer(remainingTime);
                player.play();
                isPaused = false;
                pauseButton.setText("Pause");
            }
        });

        // Stop button logic
        stopButton.setOnClickListener(v -> {
            countDownTimer.cancel();
            player.stop();
            finish(); // End the activity
        });

        // Extend button logic
        extendButton.setOnClickListener(v -> {
            countDownTimer.cancel();
            remainingTime += extendTime; // Add 5 more minutes
            startFocusTimer(remainingTime);
        });

        // Change sound logic
        changeSoundButton.setOnClickListener(v -> {
            // Change background sound based on user selection
            switchSoundBasedOnScenario();
        });

        // Custom Music button logic
        customMusicButton.setOnClickListener(v -> {
            Intent intent = new Intent(FocusModeActivity.this, AudioListActivity.class);
            startActivityForResult(intent, 1);
        });

        // Volume control logic
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                player.setVolume(progress / 100f); // Adjust volume based on SeekBar position
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startFocusTimer(long timeMillis) {
        countDownTimer = new CountDownTimer(timeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished; // Update remaining time
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Toast.makeText(FocusModeActivity.this, "Session finished", Toast.LENGTH_SHORT).show();
                notifySessionEnd();
            }
        };
        countDownTimer.start();
    }

    private void playBackgroundSound(String soundName) {
        // Get the resource ID from the file name in raw folder
        int soundResource = getSoundResourceByName(soundName);
        MediaItem mediaItem = MediaItem.fromUri("android.resource://" + getPackageName() + "/" + soundResource);
        player.setMediaItem(mediaItem);
        player.setRepeatMode(Player.REPEAT_MODE_ALL); // Loop the sound until the session ends
        player.prepare();
        player.play();
    }

    private int getSoundResourceByName(String soundName) {
        Resources res = getResources();
        return res.getIdentifier(soundName, "raw", getPackageName());
    }

    private void switchSoundBasedOnScenario() {
        String selectedScenario = scenarioSpinner.getSelectedItem().toString();
        switch (selectedScenario) {
            case "Work":
                currentSound = "coffee_shop";
                break;
            case "Study":
                currentSound = "rain";
                break;
            case "Reading":
                currentSound = "white_noise";
                break;
            default:
                currentSound = "white_noise";
                break;
        }
        playBackgroundSound(currentSound);
    }

    private void notifySessionEnd() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Toast.makeText(this, "Session finished! Time to take a break.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String selectedAudio = data.getStringExtra("selectedAudio");
            playBackgroundSound(selectedAudio);
        }
    }
}
