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
    private Button goToForumButton, logOutButton, goToMeditationButton, goToProfileButton;
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
        goToMeditationButton = findViewById(R.id.goToMeditationButton);  // 初始化冥想按钮

        goToProfileButton = findViewById(R.id.goToProfileButton);

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

            // 启动 ForumActivity
            startActivity(intent);
        });

        //Profile
        goToProfileButton.setOnClickListener(v -> {
            // 创建 Intent 跳转到 ProfileActivity
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);

            // 启动 ForumActivity
            startActivity(intent);
        });

        // 为 Go to Meditation 按钮设置监听器
        goToMeditationButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MeditationVideoListActivity.class);
            startActivity(intent);  // 启动冥想界面
        });
    }

}
