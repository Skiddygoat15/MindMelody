package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Adapter.HomeAdapter;

import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private Button goToForumButton, goToWhiteNoiseButton, logOutButton, goToProfileButton, goToSleepAssistButton;
    private Button focusModeButton, goToMeditationButton;

    private HomeAdapter homeAdapter;
    private List<String> homeContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load home_activity.xml layout
        setContentView(R.layout.home_activity);

        // Get Button View
        goToForumButton = findViewById(R.id.goToForumButton);
        goToWhiteNoiseButton = findViewById(R.id.goToWhiteNoiseButton);
        focusModeButton = findViewById(R.id.goToFocusButton);
        logOutButton = findViewById(R.id.logOutButton);
        goToMeditationButton = findViewById(R.id.goToMeditationButton);

        goToProfileButton = findViewById(R.id.goToProfileButton);
        goToSleepAssistButton = findViewById(R.id.goToSleepAssistButton);

        // Getting globally passed user information
        User loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter with the user's first name
        if (loggedInUser != null) {
            homeAdapter = new HomeAdapter(this, loggedInUser.getFirstName());
        } else {
            homeAdapter = new HomeAdapter(this, "Guest");
        }

        recyclerView.setAdapter(homeAdapter);

        // Check the display state of the RecyclerView and reinitialize it if necessary.
        checkRecyclerViewVisibility();

        // Button listeners
        goToForumButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ForumActivity.class);

            startActivity(intent);
        });

        goToWhiteNoiseButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AudioListActivity.class);

            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
        });

        focusModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FocusModeActivity.class);
            startActivity(intent);
        });

        logOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);

            startActivity(intent);
        });

        goToProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);

            startActivity(intent);
        });

        goToSleepAssistButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SleepActivity.class);

            startActivity(intent);
        });

        goToMeditationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MeditationVideoListActivity.class);
            startActivity(intent);
        });
    }

    private void checkRecyclerViewVisibility() {
        // Check if the RecyclerView is visible
        if (recyclerView.getVisibility() != View.VISIBLE) {
            // If it's not visible, try reinitializing the RecyclerView.
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(homeAdapter); // Reset the adapter
            recyclerView.invalidate(); // Force the RecyclerView to redraw.
        }
    }


}
