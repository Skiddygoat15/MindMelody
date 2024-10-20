package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.R;

import java.util.Date;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, passwordConfirmEditText;
    private Button createUserButton;
    private TextView loginLinkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        // Get the view in the layout
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);  // 假设你在布局文件中添加了此 EditText
        createUserButton = findViewById(R.id.createUserButton);
        loginLinkText = findViewById(R.id.loginLinkText);

        // Set the ‘Create User’ button click event.
        createUserButton.setOnClickListener(v -> {
            if (validateFields()) {
                registerUser();
            }
        });

        // Set the ‘Log in’ text click event to jump to the login page.
        loginLinkText.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(loginIntent);
        });
    }

    // Verify that all input fields are correct
    private boolean validateFields() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = passwordConfirmEditText.getText().toString();

        // Verify First Name and Last Name
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || containsPunctuation(firstName) || containsPunctuation(lastName)) {
            Toast.makeText(this, "First Name and Last Name cannot be empty or contain punctuation!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate the Email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verify the password
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Password cannot be empty and must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verify the confirmation password
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The passwords do not match, please check!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Register User Logic
    private void registerUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // Encrypt passwords with Bcrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // Get a UserDB instance
        UserDB db = UserDB.getDatabase(getApplicationContext());

        // Use Room's query thread pool to check for the existence of a mailbox
        db.getQueryExecutor().execute(() -> {
            int emailExists = db.userDao().checkEmailExists(email);

            runOnUiThread(() -> {
                if (emailExists > 0) {
                    Toast.makeText(this, "The email you provided is already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(0, email, hashedPassword, firstName, lastName, new Date(), null, "android.resource://com.devsquad.mind_melody/2131755013");

                    db.getQueryExecutor().execute(() -> {
                        long userId = db.userDao().registerUser(user);

                        runOnUiThread(() -> {
                            if (userId > 0) {
                                Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                Toast.makeText(this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            });
        });
    }


    // Check for punctuation
    private boolean containsPunctuation(String str) {
        Pattern pattern = Pattern.compile("\\p{Punct}");
        return pattern.matcher(str).find();
    }
}
