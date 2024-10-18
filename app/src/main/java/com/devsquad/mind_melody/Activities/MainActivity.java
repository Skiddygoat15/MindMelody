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

        // 验证输入框不为空
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用 Room 提供的查询线程池来执行查询任务
        UserDB db = UserDB.getDatabase(getApplicationContext());

        // 使用 UserDB 中定义的查询线程池
        db.getQueryExecutor().execute(() -> {
            User user = db.userDao().getUserByEmail(email);  // 查询用户信息

            // 回到主线程更新 UI
            runOnUiThread(() -> {
                if (user == null) {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                } else {
                    // 验证密码是否正确
                    if (BCrypt.checkpw(password, user.getUserPassword())) {
                        // 用户存在，跳转到 HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("loggedInUser", user);  // 将 User 对象传递给 HomeActivity
                        ((MyApplication) getApplicationContext()).setLoggedInUser(user);
                        startActivity(intent);
                        finish();  // 结束当前 Activity，防止用户返回登录页面
                    } else {
                        Toast.makeText(this, "User information incorrect, please try again or register a new account.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}