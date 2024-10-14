package com.devsquad.mind_melody.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.PostDetailAdapter;
import com.devsquad.mind_melody.Adapter.ReplyAdapter;
import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;
import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.Model.Forum.Reply;
import com.devsquad.mind_melody.Model.Forum.ReplyDao;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private TextView postTitle, postAuthor, postContent, postDate, backTo;
    private EditText replyContentEditText;
    private Button replyButton;
    private RecyclerView replyRecyclerView, recyclerViewPostDetails;
    private List<Reply> replyList;
    private ReplyAdapter replyAdapter;
    private ForumDB forumDB;
    private PostDao postDao;
    private ReplyDao replyDao;
    private User loggedInUser; // 当前登录的用户
    private int postId; // 当前帖子的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        recyclerViewPostDetails = findViewById(R.id.recyclerViewPostDetails);  // 初始化RecyclerView

        // 初始化UI组件
        replyContentEditText = findViewById(R.id.replyInput);
        replyButton = findViewById(R.id.replyButton);
        replyRecyclerView = findViewById(R.id.recyclerViewReplies);
        backTo = findViewById(R.id.BackTo); // 返回按钮

        // 获取传入的postId和loggedInUser
        postId = getIntent().getIntExtra("postId", -1);
        loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");

        // 获取数据库实例
        forumDB = ForumDB.getDatabase(this);
        postDao = forumDB.postDao();
        replyDao = forumDB.replyDao();

        // 加载帖子数据
        loadPost(postId);

        // 加载回复数据
        loadReplies(postId);

        // 设置返回按钮的监听器，返回到ForumActivity
        backTo.setOnClickListener(v -> {
            Intent intent = new Intent(PostActivity.this, ForumActivity.class);
            intent.putExtra("loggedInUser", loggedInUser); // 传递当前用户信息
            startActivity(intent);
            finish(); // 销毁当前Activity，回到ForumActivity
        });

        // 设置回复按钮监听器
        replyButton.setOnClickListener(v -> {
            String replyContent = replyContentEditText.getText().toString().trim();

            // 字段验证
            if (validateReply(replyContent)) {
                // 创建新的 Reply 对象
                Reply newReply = new Reply(postId, replyContent, loggedInUser.getFirstName(), new Date());

                // 使用线程方法插入新回复
                new Thread(() -> {
                    long replyId = replyDao.insertReply(newReply); // 插入回复

                    if (replyId > 0) {
                        // 回复插入成功后刷新回复列表
                        runOnUiThread(() -> {
                            Toast.makeText(PostActivity.this, "Reply posted successfully!", Toast.LENGTH_SHORT).show();
                            loadReplies(postId); // 刷新回复列表

                            // 清空输入框内容
                            replyContentEditText.setText("");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(PostActivity.this, "Failed to post reply!", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }

    // 加载帖子并显示
    private void loadPost(int postId) {
        new Thread(() -> {
            Post post = postDao.getPostById(postId);  // 从数据库获取帖子数据

            if (post != null) {
                runOnUiThread(() -> {
                    // 创建一个Post列表并将帖子添加到列表中
                    List<Post> postDetails = new ArrayList<>();
                    postDetails.add(post);

                    // 创建PostDetailAdapter并设置RecyclerView
                    PostDetailAdapter postDetailAdapter = new PostDetailAdapter(PostActivity.this, postDetails);
                    recyclerViewPostDetails.setLayoutManager(new LinearLayoutManager(PostActivity.this));
                    recyclerViewPostDetails.setAdapter(postDetailAdapter);  // 绑定Adapter到RecyclerView
                });
            }
        }).start();
    }


    // 加载回复并更新RecyclerView
    private void loadReplies(int postId) {
        new Thread(() -> {
            replyList = replyDao.getRepliesByPostId(postId);

            runOnUiThread(() -> {
                replyAdapter = new ReplyAdapter(PostActivity.this, replyList);
                replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                replyRecyclerView.setAdapter(replyAdapter);
            });
        }).start();
    }

    // 验证回复内容
    private boolean validateReply(String replyContent) {
        if (TextUtils.isEmpty(replyContent)) {
            Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (replyContent.length() < 3) {
            Toast.makeText(this, "Reply must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (replyContent.startsWith(" ")) {
            Toast.makeText(this, "Reply cannot start with a space", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
