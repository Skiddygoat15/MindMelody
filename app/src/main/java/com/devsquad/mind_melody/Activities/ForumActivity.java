package com.devsquad.mind_melody.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Activities.OverallApplicationSetups.MyApplication;
import com.devsquad.mind_melody.Adapter.PostAdapter;
import com.devsquad.mind_melody.Model.Forum.ForumDB;
import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.Model.Forum.PostDao;
import com.devsquad.mind_melody.Model.User.User;
import com.devsquad.mind_melody.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ForumDB forumDB;
    private EditText postTitle;
    private EditText postContent;
    private Button postButton, returnButton;
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
        returnButton = findViewById(R.id.returnButton);

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
                            savePostLocal(newPost);

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
        returnButton.setOnClickListener(v -> {
            // 创建Intent对象，用于跳转到HomeActivity
            Intent intent = new Intent(ForumActivity.this, HomeActivity.class);

            // 如果需要传递当前用户信息，您可以添加如下代码
            intent.putExtra("loggedInUser", loggedInUser);

            // 启动HomeActivity
            startActivity(intent);

            // 结束当前Activity，防止返回时回到ForumActivity
            finish();
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

    // 新增的 savePostLocal 方法
    private void savePostLocal(Post post) {
        // 定义文件名：作者名-当前时间
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = post.getAuthor() + "-" + timestamp + ".txt";

        // 定义文件夹路径
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ForumPosts");
        if (!dir.exists()) {
            dir.mkdirs();  // 创建文件夹
        }

        // 创建文件
        File file = new File(dir, fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Author: ").append(post.getAuthor()).append("\n");
            writer.append("Title: ").append(post.getTitle()).append("\n");
            writer.append("Content: ").append(post.getContent()).append("\n");
            writer.append("Created At: ").append(post.getCreatedAt().toString()).append("\n");
            writer.flush();
            writer.close();

            // 上传文件到 Firebase Storage
            if(canUpload())
                uploadFileToFirebase(file);

        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(ForumActivity.this, "Failed to save post locally!", Toast.LENGTH_SHORT).show();
        }
    }

    // 上传文件到 Firebase Storage
    private void uploadFileToFirebase(File file) {
        // 获取 Firebase Storage 实例
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // 以日期为文件夹名称，将文件存储到该文件夹中
        StorageReference fileRef = storageRef.child(currentDate + "/" + file.getName());

        // 上传文件
        UploadTask uploadTask = fileRef.putFile(Uri.fromFile(file));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // 上传成功后删除本地文件
            if (file.delete()) {
            } else {
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }
    // 新增 canUpload 方法
    private boolean canUpload() {
        // 检查电量是否大于30%
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = 0;
        int scale = 0;

        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        float batteryPct = level * 100 / (float) scale;

        // 检查网络是否为Wi-Fi
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        boolean isWifi = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        // 只有当电量大于30%且连接到Wi-Fi时才允许上传
        return batteryPct > 30 && isWifi;
    }

}
