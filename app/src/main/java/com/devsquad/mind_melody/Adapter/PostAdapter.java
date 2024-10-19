package com.devsquad.mind_melody.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devsquad.mind_melody.Model.Forum.Post;
import com.devsquad.mind_melody.R;
import com.devsquad.mind_melody.Adapter.SimpleDateFormatter.DateUtils;
import com.devsquad.mind_melody.Model.Forum.PostDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;
    private PostDao postDao;  // Database DAO
    private Set<Integer> likedPosts = new HashSet<>();  // Track post IDs that users like

    // Define the interface
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    // Instances of the interface
    private OnItemClickListener onItemClickListener;

    // Setting up listener methods
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public PostAdapter(Context context, List<Post> postList, PostDao postDao) {
        this.context = context;
        this.postList = postList;
        this.postDao = postDao;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.author.setText(post.getAuthor());
        holder.title.setText(post.getTitle());
        holder.content.setText(post.getContent());

        // Use DateUtils to format createdAt.
        String formattedDate = DateUtils.formatDateToSydneyTime(post.getCreatedAt());
        holder.createdAt.setText(formattedDate);  // Formatted time display
        holder.likesNum.setText(post.getLikesNum() + " Likes");

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(post);  // Pass the click event through the interface
            }
        });

        // Set the initial state of the Like icon
        if (likedPosts.contains(post.getPostId())) {
            holder.likeIcon.setImageResource(R.drawable.ic_thumb_up_clicked);  // Show Liked Icon
        } else {
            holder.likeIcon.setImageResource(R.drawable.ic_thumb_up);  // Show unliked icon
        }

        // Set the click event for the Like icon
        holder.likeIcon.setOnClickListener(v -> {
            if (likedPosts.contains(post.getPostId())) {
                // Unliked if already liked
                CompletableFuture.runAsync(() -> {
                    postDao.disLikePost(post.getPostId());  // Call the DAO method that un-likes the
                    post.setLikesNum(post.getLikesNum() - 1);  // Update local likes
                }).thenRun(() -> {
                    // 更新UI
                    likedPosts.remove(post.getPostId());  // Update the collection of tracked likes
                    holder.likesNum.post(() -> holder.likesNum.setText(post.getLikesNum() + " Likes"));  // Update likes num
                    holder.likeIcon.post(() -> holder.likeIcon.setImageResource(R.drawable.ic_thumb_up));  // Switch back to the Unliked icon
                });
            } else {
                // If there are no likes, do a like
                CompletableFuture.runAsync(() -> {
                    postDao.likePost(post.getPostId());  // Call the DAO method for the likes
                    post.setLikesNum(post.getLikesNum() + 1);  // Update local likes
                }).thenRun(() -> {
                    // Update UI
                    likedPosts.add(post.getPostId());  // Update the collection of tracked likes
                    holder.likesNum.post(() -> holder.likesNum.setText(post.getLikesNum() + " Likes"));  // Update the number of likes
                    holder.likeIcon.post(() -> holder.likeIcon.setImageResource(R.drawable.ic_thumb_up_clicked));  // Switch to the liked icon
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        TextView author, title, content, createdAt, likesNum;
        ImageView likeIcon;  // Add a like icon

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            createdAt = itemView.findViewById(R.id.createdAt);
            likesNum = itemView.findViewById(R.id.likesNum);
            likeIcon = itemView.findViewById(R.id.likeIcon);
        }
    }
}