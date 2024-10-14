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

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, passwordConfirmEditText;
    private Button createUserButton;
    private TextView loginLinkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        // 获取布局中的视图
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);  // 假设你在布局文件中添加了此 EditText
        createUserButton = findViewById(R.id.createUserButton);
        loginLinkText = findViewById(R.id.loginLinkText);

        // 设置 "Create User" 按钮点击事件
        createUserButton.setOnClickListener(v -> {
            if (validateFields()) {  // 验证用户输入字段
                registerUser();
            }
        });

        // 设置 "Log in" 文本点击事件，跳转到登录页面
        loginLinkText.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
            startActivity(loginIntent);
        });
    }

    // 验证所有输入字段的正确性
    private boolean validateFields() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = passwordConfirmEditText.getText().toString();

        // 验证 First Name 和 Last Name
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || containsPunctuation(firstName) || containsPunctuation(lastName)) {
            Toast.makeText(this, "First Name and Last Name cannot be empty or contain punctuation!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证 Email 格式
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证密码
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Password cannot be empty and must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The passwords do not match, please check!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // 注册用户逻辑
    private void registerUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        // 启动后台线程执行数据库操作
        new Thread(() -> {
            UserDB db = UserDB.getDatabase(getApplicationContext());

            // 检查邮箱是否已存在
            int emailExists = db.userDao().checkEmailExists(email);

            // 回到主线程更新 UI
            runOnUiThread(() -> {
                if (emailExists > 0) {
                    Toast.makeText(this, "The email you provided is already in use!", Toast.LENGTH_SHORT).show();
                } else {
                    // 构建 User 对象
                    User user = new User(0, email, password, firstName, lastName, new Date(), null);

                    // 在后台线程中注册用户
                    new Thread(() -> {
                        long userId = db.userDao().registerUser(user);

                        runOnUiThread(() -> {
                            // 如果注册成功，跳转到 MainActivity
                            if (userId > 0) {
                                Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(loginIntent);
                                finish();
                            } else {
                                Toast.makeText(this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                }
            });
        }).start();  // 启动线程
    }


    // 检查是否包含标点符号
    private boolean containsPunctuation(String str) {
        Pattern pattern = Pattern.compile("\\p{Punct}");
        return pattern.matcher(str).find();
    }
}
