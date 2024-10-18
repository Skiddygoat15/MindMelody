package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Adapter.HomeAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button goToForumButton, logOutButton, goToSleepAssistButton;
    private HomeAdapter homeAdapter;
    private List<String> homeContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载 home_activity.xml 布局
        setContentView(R.layout.home_activity);

        // 获取按钮视图
        goToForumButton = findViewById(R.id.goToForumButton);
        logOutButton = findViewById(R.id.logOutButton);
        goToSleepAssistButton = findViewById(R.id.goToSleepAssistButton);

        // 获取 MainActivity 传递过来的用户信息
        User loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");

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

        // 为 Go to Forum 按钮设置监听器
        goToForumButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ForumActivity
            Intent intent = new Intent(HomeActivity.this, ForumActivity.class);

            // 将 loggedInUser 传递给 ForumActivity
            intent.putExtra("loggedInUser", loggedInUser);

            // 启动 ForumActivity
            startActivity(intent);
        });

        // 为 Log Out 按钮设置监听器
        logOutButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ForumActivity
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);

            // 启动 MainActivity
            startActivity(intent);
        });


        // 为 sleepactivity 按钮设置监听器
        goToSleepAssistButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ForumActivity
            Intent intent = new Intent(HomeActivity.this, SleepActivity.class);

            // 启动 SleepActivity
            startActivity(intent);
        });
    }

}
