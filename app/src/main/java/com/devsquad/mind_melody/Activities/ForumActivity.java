package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.PostAdapter;
import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ForumDB forumDB;
    private EditText postTitle;
    private EditText postContent;
    private Button postButton;
    private PostDao postDao;
    private User loggedInUser; // 当前登录的用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        // 获取从 MyApplication 设置的全局 loggedInUser
        loggedInUser = ((MyApplication) getApplicationContext()).getLoggedInUser();

        if (loggedInUser != null) {
            Log.d("ForumActivity", "Logged in User ID: " + loggedInUser.getUserId());
        } else {
            Log.d("ForumActivity", "No user found, loggedInUser is null");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postButton = findViewById(R.id.postButton);

        // 获取数据库实例
        forumDB = ForumDB.getDatabase(this);
        postDao = forumDB.postDao();

        // 使用Thread方法加载帖子
        loadPosts();

        // 设置发布按钮监听器
        postButton.setOnClickListener(v -> {
            String title = postTitle.getText().toString().trim();
            String content = postContent.getText().toString().trim();

            // 字段验证
            if (validatePost(title, content)) {
                // 创建新帖子对象，使用 loggedInUser 的 firstName 作为 author，当前系统时间作为 postDate
                Post newPost = new Post(title, content, loggedInUser.getFirstName(), new Date(), 0, loggedInUser.getUserId());

                // 使用线程方法插入新帖子
                new Thread(() -> {
                    long postId = postDao.insertPost(newPost); // 插入帖子

                    if (postId > 0) {
                        // 帖子插入成功后刷新帖子列表
                        runOnUiThread(() -> {
                            Toast.makeText(ForumActivity.this, "Post published successfully!", Toast.LENGTH_SHORT).show();
                            loadPosts(); // 刷新帖子列表

                            // 清空输入框内容
                            postTitle.setText("");
                            postContent.setText("");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ForumActivity.this, "Failed to publish post!", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });

        // 设置刷新按钮监听器
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadPosts());
    }

    // 加载帖子并更新RecyclerView
    private void loadPosts() {
        new Thread(() -> {
            // 在后台线程中获取帖子数据
            postList = postDao.getAllPosts();

            // 回到主线程更新RecyclerView
            runOnUiThread(() -> {
                postAdapter = new PostAdapter(ForumActivity.this, postList, postDao);
                recyclerView.setAdapter(postAdapter);
                // 设置点击事件监听器
                postAdapter.setOnItemClickListener(post -> {
                    Intent intent = new Intent(ForumActivity.this, PostActivity.class);
                    intent.putExtra("postId", post.getPostId());  // 传递帖子ID
                    intent.putExtra("loggedInUser", loggedInUser);  // 传递当前登录用户
                    startActivity(intent);
                });
            });
        }).start();
    }

    // 验证帖子标题和内容
    private boolean validatePost(String title, String content) {
        // 检查标题和内容是否为空或过短，且不能以空格开头
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Title and content cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (title.length() < 3 || content.length() < 3) {
            Toast.makeText(this, "Title and content must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (title.startsWith(" ") || content.startsWith(" ")) {
            Toast.makeText(this, "Title and content cannot start with a space", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
