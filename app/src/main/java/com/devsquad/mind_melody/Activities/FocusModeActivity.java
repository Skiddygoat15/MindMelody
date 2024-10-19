package com.devsquad.mind_melody.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import android.widget.ProgressBar;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.lang.reflect.Field;

public class FocusModeActivity extends AppCompatActivity {

    private TextView timerText;
    private Button pauseButton, extendButton, startStopButton, changeSoundButton, customMusicButton, changeFavoriteButton;
    private ImageButton backButton;
    private SeekBar volumeSeekBar;
    private Spinner scenarioSpinner;
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean isRunning = false;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private User loggedInUser;

    // Constants
    private final long extendTime = 5 * 60 * 1000; // Extend by 5 minutes
    private long totalTime = 6 * extendTime; // Default time: 30 minutes in milliseconds
    private long remainingTime = 6 * extendTime;
    private String currentSound = null;
    private int currentIndex = 0; // store the index of the currently playing audio

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_mode);

        // Initialize UI components
        timerText = findViewById(R.id.timer_text);
        pauseButton = findViewById(R.id.button_pause);
        extendButton = findViewById(R.id.button_extend);
        startStopButton = findViewById(R.id.start_time);
        changeSoundButton = findViewById(R.id.button_change_sound);
        customMusicButton = findViewById(R.id.button_custom_music);
        changeFavoriteButton = findViewById(R.id.button_change_favorite);
        backButton = findViewById(R.id.back_button);
        volumeSeekBar = findViewById(R.id.seekbar_volume);
        scenarioSpinner = findViewById(R.id.spinner_scenario);
        progressBar = findViewById(R.id.circularProgressBar);

        // Initialize ExoPlayer for background sounds
        player = new ExoPlayer.Builder(this).build();

        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();
        UserDB userDB = UserDB.getDatabase(this);
        UserDao userDao = userDB.userDao();

        // Set initial timer value
        updateTimerText(remainingTime);

        // Disable buttons before starting
        changeSoundButton.setEnabled(false);
        customMusicButton.setEnabled(false);
        changeFavoriteButton.setEnabled(true);
        backButton.setEnabled(true);
        pauseButton.setEnabled(false);
        extendButton.setEnabled(false);

        // Start and Set Time logic with scroll picker in one button
        startStopButton.setOnClickListener(v -> {
            if (!isRunning) {
                // Show the time picker when the session hasn't started
                showTimePicker();
            } else {
                // Stop the session
                new AlertDialog.Builder(FocusModeActivity.this)
                        .setTitle("Stop Session")
                        .setMessage("Are you sure you want to stop this session?")
                        .setPositiveButton("Yes", (dialog, which) -> resetSession())
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // Pause button logic
        pauseButton.setOnClickListener(v -> {
            if (!isPaused) {
                countDownTimer.cancel();
                player.pause();
                isPaused = true;
                pauseButton.setText("Resume");
            } else {
                startFocusTimer(totalTime, remainingTime);
                player.play();
                isPaused = false;
                pauseButton.setText("Pause");
            }
        });

        // Change sound button logic (Jukebox functionality)
        changeSoundButton.setOnClickListener(v -> playRandomSound());

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

        // Extend button logic
        extendButton.setOnClickListener(v -> {
            remainingTime += extendTime;  // Add the extension time to remaining time
            totalTime += extendTime;// Also update total time accordingly
            startFocusTimer(totalTime, remainingTime);
        });


        // Spinner logic for changing mode and adjusting time and sound
        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                resetSession();
                switchSoundBasedOnScenario();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isRunning) {
                    Toast.makeText(FocusModeActivity.this, "Stop the session first", Toast.LENGTH_SHORT).show();
                } else {
                    finish(); // Or use getActivity().finish() if used in Fragment
                }
            }
        });

        // Back button logic
        backButton.setOnClickListener(v -> {
            if (isRunning) {
                // If a session is in progress, a dialog box is displayed asking you to stop the session
                new AlertDialog.Builder(FocusModeActivity.this)
                        .setTitle("Stop Session")
                        .setMessage("Are you sure you want to stop this session?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // reset the session status
                            resetSession();
                            // return to home page
                            Intent intent = new Intent(FocusModeActivity.this, HomeActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                // If the session does not start, return to home page directly
                Intent intent = new Intent(FocusModeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Get Favourite Music from database and play
        customMusicButton.setOnClickListener(v -> {
            // Pause audio from the ExoPlayer
            if (player != null && player.isPlaying()) {
                player.pause();
            }

            // Obtain the favouriteMusic from the database and play it
            new Thread(() -> {
                String favouriteMusic = userDao.getFavouriteMusic(loggedInUser.getUserId());
                runOnUiThread(() -> {
                    if (favouriteMusic != null) {
                        playBackgroundSound(favouriteMusic);
                    } else {
                        Toast.makeText(FocusModeActivity.this, "No favorite audio set", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // Change favorite button logic (goes to audio list page)
        changeFavoriteButton.setOnClickListener(v -> {
            // Pause the music
            if (player != null && player.isPlaying()) {
                player.pause();  // Pause the ExoPlayer music
            }

            // Pause the countdown timer
            if (countDownTimer != null) {
                countDownTimer.cancel();  // Cancel the ongoing timer
                isPaused = true;  // Mark that the session is paused
            }

            // Navigate to the audio list page
            Intent intent = new Intent(FocusModeActivity.this, AudioListActivity.class);
            startActivity(intent);
        });
    }

    // Time picker for selecting time between 0-4 hours with minutes and starting the session
    private void showTimePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FocusModeActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);

        NumberPicker hourPicker = dialogView.findViewById(R.id.hour_picker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minute_picker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(4); // Limit to 4 hours

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59); // Limit minutes to 59

        // Initialize the pickers with current remaining time
        long currentMinutes = remainingTime / 1000 / 60;
        int initialHour = (int) (currentMinutes / 60);
        int initialMinute = (int) (currentMinutes % 60);

        hourPicker.setValue(initialHour);
        minutePicker.setValue(initialMinute);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            // User clicked OK, start the session
            int selectedHour = hourPicker.getValue();
            int selectedMinute = minutePicker.getValue();

            // Convert the picked time into milliseconds and update both totalTime and remainingTime
            remainingTime = (selectedHour * 60 * 60 * 1000) + (selectedMinute * 60 * 1000);
            totalTime = remainingTime; // Update totalTime based on user's choice

            // Start the session
            startFocusTimer(totalTime, remainingTime);
            playBackgroundSound(currentSound);  // Start the background music
            startStopButton.setText("Stop");    // Change button to "Stop"
            isRunning = true;
            pauseButton.setEnabled(true);
            changeSoundButton.setEnabled(true);
            customMusicButton.setEnabled(true);
            extendButton.setEnabled(true);
            changeFavoriteButton.setEnabled(false);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // If the user clicks Cancel, do nothing (stay on "Start")
            startStopButton.setText("Start");
            isRunning = false;
        });

        builder.create().show();
    }

    // Recalculate the progress bar without starting the timer
    private void updateProgressBar() {
        // Progress = (time elapsed / totalTime) * 100
        int progress = (int) ((totalTime - remainingTime) * 100 / totalTime);
        progressBar.setProgress(progress);
        System.out.print(progress);
    }


    // Timer logic with progress bar
    private void startFocusTimer(long totalTimeMillis, long remainingTimeMillis) {
        totalTime = totalTimeMillis;  // Set the total time for the session

        // Cancel any existing timer if it exists
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Start a new timer with the remaining time
        countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;  // Update the remaining time
                updateTimerText(remainingTime);       // Update the timer text

                // Calculate progress based on totalTime and remainingTime
                updateProgressBar();  // Keep progress bar in sync
            }

            @Override
            public void onFinish() {
                Toast.makeText(FocusModeActivity.this, "Session finished", Toast.LENGTH_SHORT).show();
                resetSession();
            }
        };

        // Start the countdown timer
        countDownTimer.start();
    }


    // Reset the session back to default state
    private void resetSession() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        player.stop(); // Stop the background music
        remainingTime = totalTime; // Reset the remaining time to the total time
        updateTimerText(remainingTime); // Reset the timer text
        progressBar.setProgress(0); // Reset the progress bar
        startStopButton.setText("Start"); // Set button text to "Start"
        isRunning = false;
        isPaused = false;
        pauseButton.setEnabled(false); // Disable pause button
        changeSoundButton.setEnabled(false); // Disable change sound button
        customMusicButton.setEnabled(false); // Disable custom music button
        extendButton.setEnabled(false);      // Disable extend button
        changeFavoriteButton.setEnabled(true); // Re-enable change favorite button
    }


    // Updates the timer text
    private void updateTimerText(long timeInMillis) {
        long minutes = timeInMillis / 1000 / 60;
        long seconds = (timeInMillis / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Logic for switching sound and time based on selected scenario
    private void switchSoundBasedOnScenario() {
        String selectedScenario = scenarioSpinner.getSelectedItem().toString();
        switch (selectedScenario) {
            case "Work":
                currentSound = "piano";
                remainingTime = 12 * extendTime;
                break;
            case "Study":
                currentSound = "rain";
                remainingTime = 9 * extendTime;
                break;
            case "Reading":
                currentSound = "forest";
                remainingTime = 6 * extendTime;
                break;
            case "Custom":
                loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();
                UserDB userDB = UserDB.getDatabase(this);
                UserDao userDao = userDB.userDao();
                // Obtain the favouriteMusic from the database and store it to currentSound
                new Thread(() -> {
                    String favouriteMusic = userDao.getFavouriteMusic(loggedInUser.getUserId());
                    runOnUiThread(() -> {
                        if (favouriteMusic != null) {
                            currentSound = favouriteMusic;
                        } else {
                            Toast.makeText(FocusModeActivity.this, "No favorite audio set", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).start();
                remainingTime = 5 * extendTime;
                break;
        }
        updateTimerText(remainingTime);
    }

    // Play the selected sound
    private void playBackgroundSound(String soundName) {
        if (soundName.startsWith("android.resource://")) {
            // Resource file path, played using MediaItem
            MediaItem mediaItem = MediaItem.fromUri(soundName);
            player.setMediaItem(mediaItem);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.prepare();
            player.play();
        } else {
            int soundResource = getSoundResourceByName(soundName);
            if (soundResource != -1) {
                MediaItem mediaItem = MediaItem.fromUri("android.resource://" + getPackageName() + "/" + soundResource);
                player.setMediaItem(mediaItem);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                player.prepare();
                player.play();
            } else {
                Toast.makeText(this, "Sound resource not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int getSoundResourceByName(String soundName) {
        Resources res = getResources();
        return res.getIdentifier(soundName, "raw", getPackageName());
    }

    // Play random sound from raw folder
    private void playRandomSound() {
        // Create a list of all.mp3 file names
        String[] mp3Files = {
                "coffee_shop",
                "forest",
                "piano",
                "quiet",
                "rain",
                "sea",
                "wind"
        };

        if (mp3Files.length > 0) {
            // Plays the audio file at the current index
            String nextSound = mp3Files[currentIndex];
            playBackgroundSound(nextSound);  // play next aduio

            // Update the index to make sure play audio on sequence
            // When the index reaches the end of the list, it is reset to 0
            currentIndex = (currentIndex + 1) % mp3Files.length;
        } else {
            Toast.makeText(this, "No MP3 files found", Toast.LENGTH_SHORT).show();
        }
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
