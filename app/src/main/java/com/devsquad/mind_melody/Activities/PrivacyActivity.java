package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.R;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_privacy);



        // Set up back button functionality
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle what happens when the user accepts the privacy policy
                // For example, navigate to the main screen of the app
                Intent intent = new Intent(PrivacyActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}