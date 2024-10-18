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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;

public class ProfileActivity extends AppCompatActivity {


    private Button logoutButton;
    private ImageView backButton;

    private UserDB userDB;
    private UserDao userDao;
    private User userID;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);


        MyApplication myApp = (MyApplication) getApplication();

        // 获取当前登录的用户
        User loggedInUser = myApp.getLoggedInUser();
        //updateUIWithUserData(loggedInUser);


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
        //if (profileImagePath != null) {
            // 这里假设你使用Glide或其他库来加载图片
            // Glide.with(this).load(profileImagePath).into(userProfileImage);
        //}


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

        // 获取当前登录的用户
        MyApplication myApp = (MyApplication) getApplication();
        User loggedInUser = myApp.getLoggedInUser();

        if (loggedInUser != null) {
            // 设置用户名称和电子邮件
            String name = loggedInUser.getFirstName() + " " + loggedInUser.getLastName();
            userInfo.put("name", name);
            userInfo.put("email", loggedInUser.getUserEmail());

            // 1. 获取当前日期和注册日期的时间差
            Date registerDate = loggedInUser.getRegisterDate();
            String registerDateStr = formatDate(registerDate);
            userInfo.put("membership", "Member since: " + registerDateStr);

            // 2. 获取当前日期和最后冥想日期的时间差
            Date lastMeditDate = loggedInUser.getLastMeditDate();
            String lastMeditationDifference = calculateTimeDifference(lastMeditDate);
            userInfo.put("lastMeditation", "Last meditation: " + lastMeditationDifference);
        } else {
            // 如果没有用户登录，使用默认数据
            userInfo.put("name", "Guest");
            userInfo.put("email", "guest@example.com");
            userInfo.put("membership", "Member since: N/A");
            userInfo.put("lastMeditation", "Last meditation: N/A");
        }

        userInfo.put("profileImage", ""); // 可自定义头像路径

        return userInfo;
    }



    // 更新界面数据
    private void updateUIWithUserData(User user) {
        TextView userName = findViewById(R.id.userName);
        TextView userEmail = findViewById(R.id.userEmail);
        TextView userMembership = findViewById(R.id.userMembership);
        TextView lastMeditation = findViewById(R.id.lastMeditation);

        // 设置用户数据
        userName.setText(user.getFirstName() + " " + user.getLastName());
        userEmail.setText(user.getUserEmail());
        userMembership.setText("Member since: " + user.getRegisterDate().toString());
        lastMeditation.setText("Last meditation: " + user.getLastMeditDate().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁数据库实例
        UserDB.destroyInstance();
    }

    // 辅助函数：格式化日期为字符串
    private String formatDate(Date date) {
        if (date == null) {
            return "N/A";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(date);
    }

    // 辅助函数：计算两个日期之间的时间差
    private String calculateTimeDifference(Date pastDate) {
        if (pastDate == null) {
            return "N/A";
        }

        Date currentDate = Calendar.getInstance().getTime();
        long diffInMillis = currentDate.getTime() - pastDate.getTime();

        long daysDifference = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hoursDifference = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;

        if (daysDifference > 0) {
            return daysDifference + " days ago";
        } else if (hoursDifference > 0) {
            return hoursDifference + " hours ago";
        } else {
            return "Just now";
        }
    }
}