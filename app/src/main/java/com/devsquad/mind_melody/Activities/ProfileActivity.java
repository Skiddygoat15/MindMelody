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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private Button logoutButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);



        // 示例：获取历史记录中的用户信息数据
        Map<String, String> userInfo = getUserHistoryInfo();

        // 查找布局中的视图
        ImageView userProfileImage = findViewById(R.id.userProfileImage);
        TextView userName = findViewById(R.id.userName);
        TextView userEmail = findViewById(R.id.userEmail);
        TextView userMembership = findViewById(R.id.userMembership);
        TextView lastMeditation = findViewById(R.id.lastMeditation);

        // 设置用户数据
        userName.setText(userInfo.get("name"));
        userEmail.setText(userInfo.get("email"));
        userMembership.setText(userInfo.get("membership"));
        lastMeditation.setText(userInfo.get("lastMeditation"));

        // 示例：根据历史记录加载用户头像 (需自定义逻辑)
        String profileImagePath = userInfo.get("profileImage");
        if (profileImagePath != null) {
            // 这里假设你使用Glide或其他库来加载图片
            // Glide.with(this).load(profileImagePath).into(userProfileImage);
        }


        // 示例历史记录数据，用字母表示
        List<Character> historyLetters = Arrays.asList('S', 'M', 'T', 'W', 'T', 'F', 'S');

        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));

        // Sample data for letters
        List<String> letters = Arrays.asList("M", "T", "W", "T", "F", "S", "S");
        ProfileAdapter adapter = new ProfileAdapter(letters);
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyRecyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void logoutClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void backClick(View view){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }






    // 示例：从历史记录获取用户信息 (可以替换为实际的数据来源)
    private Map<String, String> getUserHistoryInfo() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("name", "Tiga");
        userInfo.put("email", "Tiga@example.com");
        userInfo.put("membership", "Member since: Jan 2025");
        userInfo.put("lastMeditation", "Last meditation: 2 days ago");
        userInfo.put("profileImage", "");
        return userInfo;
    }
}