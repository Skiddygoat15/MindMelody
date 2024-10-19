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

    private TextView backTo;
    private EditText replyContentEditText;
    private Button replyButton;
    private RecyclerView replyRecyclerView, recyclerViewPostDetails;
    private List<Reply> replyList;
    private ReplyAdapter replyAdapter;
    private ForumDB forumDB;
    private PostDao postDao;
    private ReplyDao replyDao;
    private User loggedInUser;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
        recyclerViewPostDetails = findViewById(R.id.recyclerViewPostDetails);  // Initialize the RecyclerView

        // Initialize UI components
        replyContentEditText = findViewById(R.id.replyInput);
        replyButton = findViewById(R.id.replyButton);
        replyRecyclerView = findViewById(R.id.recyclerViewReplies);
        backTo = findViewById(R.id.BackTo);

        // Get the incoming postId and loggedInUser
        postId = getIntent().getIntExtra("postId", -1);
        loggedInUser = (User) getIntent().getSerializableExtra("loggedInUser");

        // Get the database instance
        forumDB = ForumDB.getDatabase(this);
        postDao = forumDB.postDao();
        replyDao = forumDB.replyDao();

        // Load post data
        loadPost(postId);

        // Load response data
        loadReplies(postId);

        // Set up a listener for the return button to return to ForumActivity
        backTo.setOnClickListener(v -> {
            Intent intent = new Intent(PostActivity.this, ForumActivity.class);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivity(intent);
            finish(); // Destroy the current Activity and return to the ForumActivity.
        });

        // Set up a reply button listener
        replyButton.setOnClickListener(v -> {
            String replyContent = replyContentEditText.getText().toString().trim();

            // Field validation
            if (validateReply(replyContent)) {
                Reply newReply = new Reply(postId, replyContent, loggedInUser.getFirstName(), new Date());

                // Insert a new reply using the Room thread pool
                forumDB.getQueryExecutor().execute(() -> {
                    long replyId = replyDao.insertReply(newReply); // Insert reply

                    if (replyId > 0) {
                        // Refresh the list of replies after a successful reply insertion
                        runOnUiThread(() -> {
                            Toast.makeText(PostActivity.this, "Reply posted successfully!", Toast.LENGTH_SHORT).show();
                            loadReplies(postId); // Refresh the list of replies

                            // Clear the contents of the input box
                            replyContentEditText.setText("");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(PostActivity.this, "Failed to post reply!", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
    }

    // Use the Room thread pool to load the post and display it.
    private void loadPost(int postId) {
        forumDB.getQueryExecutor().execute(() -> {
            Post post = postDao.getPostById(postId);  // Getting post data from the database

            if (post != null) {
                runOnUiThread(() -> {
                    // Create a Post list and add posts to the list
                    List<Post> postDetails = new ArrayList<>();
                    postDetails.add(post);

                    // Create the PostDetailAdapter and set up the RecyclerView.
                    PostDetailAdapter postDetailAdapter = new PostDetailAdapter(PostActivity.this, postDetails);
                    recyclerViewPostDetails.setLayoutManager(new LinearLayoutManager(PostActivity.this));
                    recyclerViewPostDetails.setAdapter(postDetailAdapter);  // Bind the Adapter to the RecyclerView.
                });
            }
        });
    }

    // Use the Room thread pool to load replies and update the RecyclerView.
    private void loadReplies(int postId) {
        forumDB.getQueryExecutor().execute(() -> {
            replyList = replyDao.getRepliesByPostId(postId);

            runOnUiThread(() -> {
                replyAdapter = new ReplyAdapter(PostActivity.this, replyList);
                replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                replyRecyclerView.setAdapter(replyAdapter);
            });
        });
    }


    // Use the Room thread pool to load replies and update the RecyclerView.
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
