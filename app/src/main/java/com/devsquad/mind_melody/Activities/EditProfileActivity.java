package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;
import org.mindrot.jbcrypt.BCrypt;


public class EditProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPassword;
    private Button saveProfileButton;
    private UserDao userDao;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        // Find the components
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // Get User
        MyApplication myApp = (MyApplication) getApplication();
        loggedInUser = myApp.getLoggedInUser();

        if (loggedInUser != null) {
            // Fill the info textview
            editFirstName.setText(loggedInUser.getFirstName());
            editLastName.setText(loggedInUser.getLastName());
            editEmail.setText(loggedInUser.getUserEmail());
            //editPassword.setText(loggedInUser.getUserPassword());
        }

        // Database
        userDao = UserDB.getDatabase(this).userDao();

        // Click Listener for save
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        // Password length limit
        if (!password.isEmpty() && password.length() <= 6) {
            Toast.makeText(EditProfileActivity.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
            return; // exit
        }

        if (loggedInUser != null) {
            loggedInUser.setFirstName(firstName);
            loggedInUser.setLastName(lastName);
            loggedInUser.setUserEmail(email);

            // Not empty to change password
            if (!password.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                loggedInUser.setUserPassword(hashedPassword);
            }

            // Update the info
            new Thread(() -> {
                if (!password.isEmpty()) {
                    // Update all
                    userDao.updateUser(
                            loggedInUser.getUserId(),
                            loggedInUser.getUserEmail(),
                            loggedInUser.getUserPassword(),
                            loggedInUser.getFirstName(),
                            loggedInUser.getLastName()
                    );
                } else {
                    // Update all except password
                    userDao.updateUser(
                            loggedInUser.getUserId(),
                            loggedInUser.getUserEmail(),
                            loggedInUser.getUserPassword(), // 使用原密码
                            loggedInUser.getFirstName(),
                            loggedInUser.getLastName()
                    );
                }

                runOnUiThread(() -> {
                    // Success Info
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Back to Profile
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Close
                });
            }).start();
        }
    }

}
