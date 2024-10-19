package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPassword;
    private Button saveProfileButton;
    private UserDao userDao;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        // 初始化视图
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // 获取当前登录的用户
        MyApplication myApp = (MyApplication) getApplication();
        loggedInUser = myApp.getLoggedInUser();

        if (loggedInUser != null) {
            // 填充当前用户信息到编辑框
            editFirstName.setText(loggedInUser.getFirstName());
            editLastName.setText(loggedInUser.getLastName());
            editEmail.setText(loggedInUser.getUserEmail());
            editPassword.setText(loggedInUser.getUserPassword());
        }

        // 初始化数据库访问
        userDao = UserDB.getDatabase(this).userDao();

        // 保存按钮的点击事件
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();

        if (loggedInUser != null) {
            loggedInUser.setFirstName(firstName);
            loggedInUser.setLastName(lastName);
            loggedInUser.setUserEmail(email);
            loggedInUser.setUserPassword(password);

            // 更新用户信息到数据库
            new Thread(() -> {
                userDao.updateUser(
                        loggedInUser.getUserId(),
                        loggedInUser.getUserEmail(),
                        loggedInUser.getUserPassword(),
                        loggedInUser.getFirstName(),
                        loggedInUser.getLastName()
                );
                runOnUiThread(() -> {
                    // 显示保存成功的提示
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // 跳转回 ProfileActivity
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // 关闭当前活动
                });
            }).start();
        }
    }
}
