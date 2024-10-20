package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.R;

import org.mindrot.jbcrypt.BCrypt;


public class MainActivity extends AppCompatActivity {


    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load activity_main.xml Layout
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Get the Sign Up Link text view
        TextView signUpLink = findViewById(R.id.signUpLink);

        // Set up a click listener for the Sign Up Link text.
        signUpLink.setOnClickListener(view -> {
            // RegistrationActivity
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // Set up a click event listener for the login button.
        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate that the input box is not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate that the input box is not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the query thread pool provided by Room to perform the query task.
        UserDB db = UserDB.getDatabase(getApplicationContext());

        // Use the query thread pool defined in UserDB.
        db.getQueryExecutor().execute(() -> {
            User user = db.userDao().getUserByEmail(email);

            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                } else {
                    // Use BCrypt to decrypt the pair to verify that the password is correct.
                    if (BCrypt.checkpw(password, user.getUserPassword())) {
                        // Password matches, jump to HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("loggedInUser", user);  // Pass the User object to the HomeActivity.
                        ((MyApplication) getApplicationContext()).setLoggedInUser(user);
                        startActivity(intent);
                        finish();  // End the current Activity to prevent the user from returning to the login page.
                    } else {
                        Toast.makeText(this, "User information incorrect, please try again or register a new account.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}