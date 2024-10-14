package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button goToForumButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载 home_activity.xml 布局
        setContentView(R.layout.home_activity);

        // 获取 TextView 视图
        welcomeText = findViewById(R.id.welcomeText);
        // 获取按钮视图
        goToForumButton = findViewById(R.id.goToForum);

        // 获取 MainActivity 传递过来的用户信息
        User loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");

        if (loggedInUser != null) {
            // 设置欢迎文本，加入用户的名字或邮箱
            welcomeText.setText("Welcome to MindMelody app, " + loggedInUser.getFirstName() + "!");
        } else {
            // 如果未能获取到用户信息，保持默认欢迎信息
            welcomeText.setText("Welcome to MindMelody app!");
        }

        // 为 Go to Forum 按钮设置监听器
        goToForumButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ForumActivity
            Intent intent = new Intent(HomeActivity.this, ForumActivity.class);

            // 将 loggedInUser 传递给 ForumActivity
            intent.putExtra("loggedInUser", loggedInUser);

            // 启动 ForumActivity
            startActivity(intent);
        });
    }
}
