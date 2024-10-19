package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.ProfileAdapter;
import com.devsquad.mind_melody.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;

public class ProfileActivity extends AppCompatActivity {


    private Button logoutButton;
    private ImageView backButton;
    private TextView editProfileButton;
    private TextView privacyButton;

    private UserDB userDB;
    private UserDao userDao;
    private User userID;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);


        // Get The user Info
        Map<String, String> userInfo = getUserHistoryInfo();

        // Find the components
        ImageView userProfileImage = findViewById(R.id.userProfileImage);
        TextView userName = findViewById(R.id.userName);
        TextView userEmail = findViewById(R.id.userEmail);
        TextView userMembership = findViewById(R.id.userMembership);
        TextView lastMeditation = findViewById(R.id.lastMeditation);

        editProfileButton = findViewById(R.id.editProfile);
        privacyButton = findViewById(R.id.privacy);

        // Set user info
        userName.setText(userInfo.get("name"));
        userEmail.setText(userInfo.get("email"));
        userMembership.setText(userInfo.get("membership"));
        lastMeditation.setText(userInfo.get("lastMeditation"));









        // Click listener for Edit Profile
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        // Click listener for Privacy Page
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, PrivacyActivity.class);
                startActivity(intent);
            }
        });


    }

        // Log out button
        public void logoutClick(View view){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // Back Button
        public void backClick(View view){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }






    // Get user Info
    private Map<String, String> getUserHistoryInfo() {
        Map<String, String> userInfo = new HashMap<>();

        // get User ID
        MyApplication myApp = (MyApplication) getApplication();
        User loggedInUser = myApp.getLoggedInUser();

        if (loggedInUser != null) {
            // Name and Email
            String name = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
            userInfo.put("name", name);
            userInfo.put("email", loggedInUser.getUserEmail());

            // Calc Member time
            Date registerDate = loggedInUser.getRegisterDate();
            String registerDateStr = formatDate(registerDate);
            userInfo.put("membership", "Member since: " + registerDateStr);

            // Get last meditation time
            Date lastMeditDate = loggedInUser.getLastMeditDate();
            String lastMeditationDifference = calculateTimeDifference(lastMeditDate);
            userInfo.put("lastMeditation", "Last meditation: " + lastMeditationDifference);
        } else {
            // Guest Default
            userInfo.put("name", "Guest");
            userInfo.put("email", "guest@example.com");
            userInfo.put("membership", "Member since: N/A");
            userInfo.put("lastMeditation", "Last meditation: N/A");
        }

        //userInfo.put("profileImage", "");

        return userInfo;
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

        UserDB.destroyInstance();
    }

    // Helper format Date to String
    private String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(date);
    }

    // Helper Calc the time difference
    private String calculateTimeDifference(Date pastDate) {
        if (pastDate == null) {
            return "N/A";
        }

        Date currentDate = Calendar.getInstance().getTime();
        long diffInMillis = currentDate.getTime() - pastDate.getTime();

        long daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;

        if (daysDifference > 0) {
            return daysDifference + " days ago";
        } else if (hoursDifference > 0) {
            return hoursDifference + " hours ago";
        } else {
            return "Just now";
        }
    }
}