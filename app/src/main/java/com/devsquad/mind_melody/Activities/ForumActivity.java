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

        // Get the global loggedInUser set from MyApplication.
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

        // Get the database instance
        forumDB = ForumDB.getDatabase(this);
        postDao = forumDB.postDao();

        // Use Room's thread pool to load posts.
        loadPosts();

        // Setting up the publish button listener
        postButton.setOnClickListener(v -> {
            String title = postTitle.getText().toString().trim();
            String content = postContent.getText().toString().trim();

            // Field validation
            if (validatePost(title, content)) {
                // Create a new post object, using loggedInUser's firstName as the author and the current system time as the postDate.
                Post newPost = new Post(title, content, loggedInUser.getFirstName(), new Date(), 0, loggedInUser.getUserId());

                // Insert a new post using the Room thread pool
                forumDB.getQueryExecutor().execute(() -> {
                    long postId = postDao.insertPost(newPost);

                    if (postId > 0) {
                        // Refresh post list after successful post insertion
                        runOnUiThread(() -> {
                            Toast.makeText(ForumActivity.this, "Post published successfully!", Toast.LENGTH_SHORT).show();
                            loadPosts();
                            savePostLocal(newPost);

                            // Clear the contents of the input box
                            postTitle.setText("");
                            postContent.setText("");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(ForumActivity.this, "Failed to publish post!", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });

        returnButton.setOnClickListener(v -> {
            // Create an Intent object to jump to the HomeActivity.
            Intent intent = new Intent(ForumActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Setting up the refresh button listener
        findViewById(R.id.refreshButton).setOnClickListener(v -> loadPosts());
    }

    // Use Room thread pool to load posts and update RecyclerView
    private void loadPosts() {
        forumDB.getQueryExecutor().execute(() -> {
            // Getting post data in a background thread
            postList = postDao.getAllPosts();

            // Return to the main thread to update the RecyclerView
            runOnUiThread(() -> {
                postAdapter = new PostAdapter(ForumActivity.this, postList, postDao);
                recyclerView.setAdapter(postAdapter);
                // Setting up the click event listener
                postAdapter.setOnItemClickListener(post -> {
                    Intent intent = new Intent(ForumActivity.this, PostActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    intent.putExtra("loggedInUser", loggedInUser);
                    startActivity(intent);
                });
            });
        });
    }


    // Validate post title and content
    private boolean validatePost(String title, String content) {
        // Check that the title and content are not empty or too short and do not start with a space
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

    // Used to save backups for current post and upload the backup to Firebase
    private void savePostLocal(Post post) {
        // Define filename: author name - current time
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = post.getAuthor() + "-" + timestamp + ".txt";

        // Define the folder path
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ForumPosts");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create the file
        File file = new File(dir, fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.append("Author: ").append(post.getAuthor()).append("\n");
            writer.append("Title: ").append(post.getTitle()).append("\n");
            writer.append("Content: ").append(post.getContent()).append("\n");
            writer.append("Created At: ").append(post.getCreatedAt().toString()).append("\n");
            writer.flush();
            writer.close();

            // Upload files to Firebase Storage
            if(canUpload())
                uploadFileToFirebase(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Upload files to Firebase Storage
    private void uploadFileToFirebase(File file) {
        // Getting a Firebase Storage Instance
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        // Store files in a folder with the date as the folder name
        StorageReference fileRef = storageRef.child(currentDate + "/" + file.getName());

        // Uploading files
        UploadTask uploadTask = fileRef.putFile(Uri.fromFile(file));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Delete local files after successful upload
            if (file.delete()) {
            } else {
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }
    // Determines whether should upload the backup
    private boolean canUpload() {
        // Check that the power level is greater than 30%
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = 0;
        int scale = 0;

        if (batteryStatus != null) {
            level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }

        float batteryPct = level * 100 / (float) scale;

        // Check if the network is Wi-Fi
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        boolean isWifi = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;

        // Uploads are only allowed when the battery is greater than 30% and you are connected to Wi-Fi
        return batteryPct > 30 && isWifi;
    }

}
