package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.Model.Forum.ReplyDao;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.Model.User.UserDB;
import com.devsquad.mind_melody.Model.User.UserDao;
import com.devsquad.mind_melody.R;
import org.mindrot.jbcrypt.BCrypt;


public class EditProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editPassword;
    private Button saveProfileButton;
    private UserDao userDao;
    private User loggedInUser;
    private ForumDB forumDB;
    private PostDao postDao;
    private ReplyDao replyDao;
    private String oldAuthor;

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

        forumDB = ForumDB.getDatabase(this);
        postDao = forumDB.postDao();
        replyDao = forumDB.replyDao();

        // 获取当前登录的用户
        MyApplication myApp = (MyApplication) getApplication();
        loggedInUser = myApp.getLoggedInUser();
        oldAuthor = loggedInUser.getFirstName();


        if (loggedInUser != null) {
            // 填充当前用户信息到编辑框
            editFirstName.setText(loggedInUser.getFirstName());
            editLastName.setText(loggedInUser.getLastName());
            editEmail.setText(loggedInUser.getUserEmail());
            //editPassword.setText(loggedInUser.getUserPassword());
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

        // 检查密码是否为空以及长度是否符合要求
        if (!password.isEmpty() && password.length() <= 6) {
            Toast.makeText(EditProfileActivity.this, "Password must be longer than 6 characters", Toast.LENGTH_SHORT).show();
            return; // 如果密码长度不符合要求，退出方法
        }

        if (loggedInUser != null) {
            loggedInUser.setFirstName(firstName);
            loggedInUser.setLastName(lastName);
            loggedInUser.setUserEmail(email);

            // 仅在密码输入框不为空时更新密码
            if (!password.isEmpty()) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                loggedInUser.setUserPassword(hashedPassword);
            }

            forumDB.getQueryExecutor().execute(() -> {
                if (!password.isEmpty()) {
                    // 更新包括密码在内的所有信息
                    userDao.updateUser(
                            loggedInUser.getUserId(),
                            loggedInUser.getUserEmail(),
                            loggedInUser.getUserPassword(),
                            loggedInUser.getFirstName(),
                            loggedInUser.getLastName()
                    );
                } else {
                    // 更新除了密码外的其他信息
                    userDao.updateUser(
                            loggedInUser.getUserId(),
                            loggedInUser.getUserEmail(),
                            loggedInUser.getUserPassword(), // 使用原密码
                            loggedInUser.getFirstName(),
                            loggedInUser.getLastName()
                    );
                }

                // 更新该用户的所有帖子中的作者名
                postDao.updatePostAuthor(loggedInUser.getFirstName(), loggedInUser.getUserId());

                // 更新回复中的作者名
                replyDao.updateReplyAuthor(loggedInUser.getFirstName(), oldAuthor);

                // 回到主线程更新UI
                runOnUiThread(() -> {
                    // 显示保存成功的提示
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // 跳转回 ProfileActivity
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // 关闭当前活动
                });
            });
        }

    }

}
