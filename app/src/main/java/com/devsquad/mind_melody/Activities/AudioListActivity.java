package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    private boolean isPlaying = false; // Indicates whether it is currently playing
    private int lastPosition = 0; // Save the paused position
    private Audio currentAudio = null; // The currently playing audio
    private User loggedInUser; // Current login user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_list);

        // RecyclerView settting
        RecyclerView recyclerView = findViewById(R.id.audio_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Initialize the play/pause button and the Stop button
        playPauseButton = findViewById(R.id.play_button);

        // Initializes the back button and sets the click event
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            // Create an Intent to jump to AudioListActivity
            Intent intent = new Intent(AudioListActivity.this, HomeActivity.class);
            // start AudioListActivity
            startActivity(intent);
        });

        // Initializes the Focus mode button and sets the click event
        focusModeButton = findViewById(R.id.focus_mode_button);
        focusModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AudioListActivity.this, FocusModeActivity.class);
            startActivity(intent);
        });

        // Initializes the audio list and sets the image resource ID for each audio
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

        // Obtain the favouriteMusic from the database and set it to the adapter
        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();
        UserDB userDB = UserDB.getDatabase(this);
        UserDao userDao = userDB.userDao();

        // Get the favouriteMusic in the database asynchronously
        new Thread(() -> {
            String favouriteMusic = userDao.getFavouriteMusic(loggedInUser.getUserId());
            runOnUiThread(() -> {
                // set default favorite audio
                if (favouriteMusic != null) {
                    for (Audio audio : audioList) {
                        if (audio.getFilePath().equals(favouriteMusic)) {
                            adapter.setDefaultAudio(audio);
                        }
                    }
                }
            });
        }).start();

        playPauseButton.setOnClickListener(v -> {
            if (currentAudio == null) {
                // If no audio is selected, the user is prompted to select one
                Toast.makeText(this, "Please select an audio before playing.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isPlaying) {
                // If it is not currently playing, continue playing
                playAudio(currentAudio);
                playPauseButton.setImageResource(R.drawable.pause_icon);  // Switch to the pause icon
                isPlaying = true;
            } else {
                // Pause the audio if it is currently playing
                pauseAudio();
                playPauseButton.setImageResource(R.drawable.play_icon);  // Switch to play icon
                isPlaying = false;
            }
        });
    }

    private void handleAudioItemClick(Audio audio) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();  // Stop the currently playing audio
            mediaPlayer.reset();  // Reset MediaPlayer, ready to play new audio
        }

        currentAudio = audio;  // Update to click audio
        playAudio(currentAudio);  // Play new audio
        playPauseButton.setImageResource(R.drawable.pause_icon);  // switch to pause icon
        isPlaying = true;  // Update playback status
    }

    private void playAudio(Audio audio) {
        // If other audio is playing, stop playing first
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, android.net.Uri.parse(audio.getFilePath()));
        mediaPlayer.setLooping(true);  // Set the audio loop

        // If you have paused before, continue playing
        mediaPlayer.seekTo(lastPosition); // Resume play from the last paused position
        mediaPlayer.start();

        // loop the audio when the audio is finished
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.seekTo(0);
            mp.start();
        });
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            lastPosition = mediaPlayer.getCurrentPosition(); // Save the paused position
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // When leave the page, pause the audio and reset MediaPlayer
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pauseAudio();
        }
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release(); // Release MediaPlayer resources
            mediaPlayer = null;    // Prevent memory leaks and clear MediaPlayer objects
        }
    }
}
