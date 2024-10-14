package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.Model.User;
import com.devsquad.mind_melody.Model.UserDB;
import com.devsquad.mind_melody.R;



public class MainActivity extends AppCompatActivity {


    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载 activity_main.xml 布局
        setContentView(R.layout.activity_main);

        // 获取输入框和按钮
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // 获取 Sign Up Link文本视图
        TextView signUpLink = findViewById(R.id.signUpLink);

        // 为 Sign Up Link文本设置点击监听器
        signUpLink.setOnClickListener(view -> {
            // 跳转到 RegistrationActivity
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // 设置登录按钮的点击事件监听器
        loginButton.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // 验证输入框不为空
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 启动后台线程执行数据库操作
        new Thread(() -> {
            UserDB db = UserDB.getDatabase(getApplicationContext());
            User user = db.userDao().loginUser(email, password);

            // 回到主线程更新 UI
            runOnUiThread(() -> {
                if (user != null) {
                    // 用户存在，跳转到 HomeActivity
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("loggedInUser", user);  // 将 User 对象传递给 HomeActivity
                    startActivity(intent);
                    finish();  // 结束当前 Activity，防止用户返回登录页面
                } else {
                    // 用户不存在，提示信息错误
                    Toast.makeText(MainActivity.this, "User information incorrect, please try again or register a new account.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();  // 启动线程
    }
}