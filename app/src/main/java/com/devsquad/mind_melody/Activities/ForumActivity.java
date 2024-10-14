package com.devsquad.mind_melody.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Adapter.PostAdapter;
import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ForumDB forumDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_activity);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取数据库实例
        forumDB = ForumDB.getDatabase(this);

        // 使用Thread方法加载帖子
        new Thread(() -> {
            // 在后台线程中获取帖子数据
            PostDao postDao = forumDB.postDao();
            postList = postDao.getAllPosts();

            // 回到主线程更新RecyclerView
            runOnUiThread(() -> {
                // 设置Adapter并绑定数据
                postAdapter = new PostAdapter(ForumActivity.this, postList);
                recyclerView.setAdapter(postAdapter);
            });
        }).start();
    }
}
